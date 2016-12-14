package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.filter.TagFilter;
import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.Feature;
import gherkin.ast.Location;
import gherkin.ast.Node;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Properties;

/**
 * Generates Cucumber runner files using configuration from FileGeneratorConfig containing parameters passed into the
 * Maven Plugin configuration.
 */

public class CucumberITGeneratorByScenario implements CucumberITGenerator {

    private final FileGeneratorConfig config;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    private int fileCounter = 1;
    private String featureFileLocation;
    private Template velocityTemplate;
    private String outputFileName;
    private final ClassNamingScheme classNamingScheme;


    /**
     * @param config The configuration parameters passed to the Maven Mojo
     * @param overriddenParameters Parameters overridden from Cucumber options VM parameter (-Dcucumber.options)
     * @param classNamingScheme The naming scheme to use for the generated class files
     */
    public CucumberITGeneratorByScenario(final FileGeneratorConfig config,
                    final OverriddenCucumberOptionsParameters overriddenParameters,
                    final ClassNamingScheme classNamingScheme) {
        this.config = config;
        this.overriddenParameters = overriddenParameters;
        this.classNamingScheme = classNamingScheme;
        initTemplate();
    }

    private void initTemplate() {
        final Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        final String name;
        if (StringUtils.isNotBlank(config.getCustomVmTemplate())) {
            // Look for custom template on the classpath or as a relative file path
            props.put("resource.loader", "class, file");
            props.put("file.resource.loader.path", config.getProjectBasedir().getAbsolutePath());
            name = config.getCustomVmTemplate();
        } else if (config.useTestNG()) {
            name = "cucumber-testng-runner.vm";
        } else {
            name = "cucumber-junit-runner.vm";
        }
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        velocityTemplate = engine.getTemplate(name, config.getEncoding());
    }

    /**
     * Generates a Cucumber runner for each scenario, or example in a scenario outline.
     *
     * @param outputDirectory the output directory to place generated files
     * @param featureFiles The feature files to create runners for
     * @throws MojoExecutionException if something goes wrong
     */
    public void generateCucumberITFiles(final File outputDirectory,
                    final Collection<File> featureFiles) throws MojoExecutionException {
        final Parser<Feature> parser = new Parser<Feature>(new AstBuilder());
        Feature feature = null;
        final TagFilter tagFilter = new TagFilter(overriddenParameters.getTags());

        for (final File file : featureFiles) {

            try {
                feature = parser.parse(new FileReader(file), new TokenMatcher());
            } catch (final FileNotFoundException e) {
                // should never happen
                // TODO - proper logging
                System.out.println(String.format("WARNING: Failed to parse '%s'...IGNORING",
                                file.getName()));
            }


            final Collection<Node> matchingScenariosAndExamples =
                            tagFilter.matchingScenariosAndExamples(feature);

            for (final Node match : matchingScenariosAndExamples) {
                outputFileName = classNamingScheme.generate(file.getName());
                setFeatureFileLocation(file, match.getLocation());
                writeFile(outputDirectory);
            }

        }
    }

    private void writeFile(final File outputDirectory) throws MojoExecutionException {
        final File outputFile = new File(outputDirectory, outputFileName + ".java");
        FileWriter w = null;
        try {
            w = new FileWriter(outputFile);
            writeContentFromTemplate(w);
        } catch (final IOException e) {
            throw new MojoExecutionException("Error creating file " + outputFile, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (final IOException e) {
                    // ignore
                    System.out.println("Failed to close file: " + outputFile);
                }
            }
        }

        fileCounter++;
    }

    /**
     * Sets the feature file location based on the given file. The full file path is trimmed to only include the
     * featuresDirectory. E.g. /myproject/src/test/resources/features/feature1.feature will be saved as
     * features/feature1.feature
     *
     * @param file The feature file
     */
    private void setFeatureFileLocation(final File file, final Location location) {
        featureFileLocation = file.getPath().replace(File.separatorChar, '/')
                        .concat(":" + location.getLine());
    }

    private void writeContentFromTemplate(final Writer writer) {

        final VelocityContext context = new VelocityContext();
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("reports", createFormatStrings());
        if (!config.filterFeaturesByTags()) {
            context.put("tags", overriddenParameters.getTags());
        }
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", config.getCucumberOutputDir());
        context.put("glue", quoteGlueStrings());
        context.put("className", FilenameUtils.removeExtension(outputFileName));
        context.put("packageName", config.getPackageName());

        velocityTemplate.merge(context, writer);
    }

    /**
     * Create the format string used for the output.
     */
    private String createFormatStrings() {
        final String[] formatStrs = overriddenParameters.getFormat().split(",");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < formatStrs.length; i++) {
            final String formatStr = formatStrs[i].trim();

            if ("pretty".equalsIgnoreCase(formatStr)) {
                sb.append("\"pretty\"");
            } else {
                sb.append(String.format("\"%s:%s/%s.%s\"", formatStr,
                                config.getCucumberOutputDir().replace('\\', '/'), fileCounter,
                                formatStr));
            }

            if (i < formatStrs.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Wraps each package in quotes for use in the template.
     */
    private String quoteGlueStrings() {
        final String[] packageStrs = overriddenParameters.getGlue().split(",");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < packageStrs.length; i++) {
            final String packageStr = packageStrs[i];
            sb.append(String.format("\"%s\"", packageStr.trim()));

            if (i < packageStrs.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
