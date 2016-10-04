package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.options.TagParser;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.Location;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.TableRow;
import gherkin.ast.Tag;
import org.apache.commons.io.FilenameUtils;
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
import java.util.List;
import java.util.Properties;

/**
 * Generates Cucumber runner files using configuration from FileGeneratorConfig containing parameters passed into the
 * Maven Plugin configuration.
 */

public class CucumberITGeneratorByScenario implements CucumberITGenerator {

    private final FileGeneratorConfig config;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    int fileCounter = 1;
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
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        if (config.useTestNG()) {
            velocityTemplate =
                            engine.getTemplate("cucumber-testng-runner.vm", config.getEncoding());
        } else {
            velocityTemplate = engine.getTemplate("cucumber-junit-runner.vm", config.getEncoding());
        }
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
        for (final File file : featureFiles) {

            try {
                feature = parser.parse(new FileReader(file), new TokenMatcher());
            } catch (final FileNotFoundException e) {
                // should never happen
                // TODO - proper logging
                System.out.println(String.format("WARNING: Failed to parse '%s'...IGNORING",
                                file.getName()));
            }


            if (shouldSkipFeature(feature)) {
                continue;
            }

            // TODO - refactor - not implemented
            for (final ScenarioDefinition scenario : feature.getScenarioDefinitions()) {

                if (scenario instanceof ScenarioOutline) {
                    final List<Examples> examples = ((ScenarioOutline) scenario).getExamples();
                    for (final Examples example : examples) {
                        for (final TableRow tableRow : example.getTableBody()) {
                            outputFileName = classNamingScheme.generate(file.getName());
                            setFeatureFileLocation(file, tableRow.getLocation());
                            writeFile(outputDirectory);

                        }

                    }
                } else {
                    outputFileName = classNamingScheme.generate(file.getName());
                    setFeatureFileLocation(file, scenario.getLocation());
                    writeFile(outputDirectory);
                }


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

    private boolean shouldSkipFeature(final Feature feature) {
        if (config.filterFeaturesByTags()) {
            if (!featureContainsMatchingTags(feature)) {
                return true;
            }
        }
        return false;
    }

    private boolean featureContainsMatchingTags(final Feature feature) {
        final List<List<String>> tagGroupsAnded =
                        TagParser.splitQuotedTagsIntoParts(overriddenParameters.getTags());
        // Tag groups are and'd together
        for (final List<String> tagGroup : tagGroupsAnded) {
            // individual tags are or'd together
            if (!featureContainsAnyTags(feature, tagGroup)) {
                return false;
            }
        }
        return true;
    }

    private boolean featureContainsAnyTags(final Feature feature, final List<String> expectedTags) {
        final List<ScenarioDefinition> scenarios = feature.getScenarioDefinitions();
        for (final String tag : expectedTags) {
            if (tag.startsWith("~")) {
                // not tags must be ignored - cannot guarantee that a feature
                // file containing an ignored tag does not contain scenarios
                // that
                // should be included
                return true;
            }
            for (final Tag actualTag : feature.getTags()) {
                if (actualTag.getName().equals(tag)) {
                    return true;
                }
            }
            for (final ScenarioDefinition scenario : scenarios) {

                for (final Tag actualTag : scenario.getTags()) {
                    if (actualTag.getName().equals(tag)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Sets the feature file location based on the given file. The full file path is trimmed to only include the
     * featuresDirectory. E.g. /myproject/src/test/resources/features/feature1.feature will be saved as
     * features/feature1.feature
     *
     * @param file The feature file
     */
    private void setFeatureFileLocation(final File file, final Location location) {
        final File featuresDirectory = config.getFeaturesDirectory();
        featureFileLocation = file.getPath()
                        .replace(featuresDirectory.getPath(), featuresDirectory.getName())
                        .replace(File.separatorChar, '/').concat(":" + location.getLine());
    }

    private void writeContentFromTemplate(final Writer writer) {

        final VelocityContext context = new VelocityContext();
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("reports", createFormatStrings());
        context.put("tags", overriddenParameters.getTags());
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", config.getCucumberOutputDir());
        context.put("glue", quoteGlueStrings());
        context.put("className", FilenameUtils.removeExtension(outputFileName));

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
            sb.append(String.format("\"%s:%s/%s.%s\"", formatStr,
                            config.getCucumberOutputDir().replace('\\', '/'), fileCounter,
                            formatStr));

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
