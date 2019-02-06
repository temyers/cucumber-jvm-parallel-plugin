package com.github.timm.cucumber.generate;

import static java.lang.String.format;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Generates Cucumber runner files using configuration from FileGeneratorConfig containing parameters passed into the
 * Maven Plugin configuration.
 * 
 * This is basically similar to CucumberITGeneratorByScenario, but generateCucumberITFiles looks up address of failed
 * scenario from rerun files instead of computing it from feature files.
 */

public class CucumberITGeneratorByRerun implements CucumberITGenerator {

    private final FileGeneratorConfig config;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    private int fileCounter = 1;
    private String featureFileLocation;
    private Template velocityTemplate;
    private String outputFileName;
    private final ClassNamingScheme classNamingScheme;

    /**
     * Constructor.
     * 
     * @param config The configuration parameters passed to the Maven Mojo
     * @param overriddenParameters Parameters overridden from Cucumber options VM parameter (-Dcucumber.options)
     * @param classNamingScheme The naming scheme to use for the generated class files
     */
    public CucumberITGeneratorByRerun(final FileGeneratorConfig config,
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
            name = "cucumber-testng-runner.java.vm";
        } else {
            name = "cucumber-junit-runner.java.vm";
        }
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        velocityTemplate = engine.getTemplate(name, config.getEncoding());
    }

    /**
     * Generates a Cucumber runner for each row in a rerun file, for all rerun files, written by Cucumber Rerun
     * formatter.
     *
     * @param outputDirectory the output directory to place generated files
     * @param rerunFiles The rerun files to create runners for
     * @throws MojoExecutionException if something goes wrong
     */
    public void generateCucumberITFiles(final File outputDirectory,
                    final Collection<File> rerunFiles) throws MojoExecutionException {

        for (final File file : rerunFiles) {
            List<String> rerunList = null;
            try {
                rerunList = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            } catch (final IOException e) {
                // should never happen
                // TODO - proper logging
                System.out.println(
                                format("WARNING: Failed to parse '%s'...IGNORING", file.getName()));
            }

            for (String rerun : rerunList) {
                String featureFileName = extractFeatureFileNameFromRerunFileLine(rerun);
                outputFileName = classNamingScheme.generate(featureFileName);
                setFeatureFileLocation(rerun);
                writeFile(outputDirectory);
            }

        }
    }

    /**
     * Each line in Rerun file contains address of failed scenario in following format:
     * 
     * features_path/feature_name.feature:<line_number_of_failed_scenario>[:<line_numbers_of_failed_scenarios>]
     * 
     * @param String one line of rerun file
     */

    private String extractFeatureFileNameFromRerunFileLine(String rerun) {
        String[] tokens = rerun.split(":");
        String featureFilePath = tokens[0];
        tokens = featureFilePath.split("/");
        String featureFileName = tokens[tokens.length - 1];
        featureFileName = featureFileName.replace(".feature", "");
        return featureFileName;
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
     * Sets the feature file location based on the given file.
     * 
     * A line in rerun contains feature path and feature name followed by scenario line number of failed scenarios.
     * Feature name and scenario line numbers are separated by :
     *
     * @param file The feature file
     */
    private void setFeatureFileLocation(String rerunFeatureAndScenarioLocation) {

        featureFileLocation = normalizePathSeparator(rerunFeatureAndScenarioLocation);
    }

    private static String normalizePathSeparator(final String file) {
        return file.replace(File.separatorChar, '/');
    }

    private void writeContentFromTemplate(final Writer writer) {
        // to escape java
        final EventCartridge ec = new EventCartridge();
        ec.addEventHandler(new EscapeJavaReference());

        final VelocityContext context = new VelocityContext();
        context.attachEventCartridge(ec);
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("plugins", createPluginStrings());
        context.put("tags", overriddenParameters.getTags());
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("glue", overriddenParameters.getGlue());
        context.put("className", FilenameUtils.removeExtension(outputFileName));
        context.put("packageName", config.getPackageName());

        velocityTemplate.merge(context, writer);
    }

    /**
     * Create the format string used for the output.
     */
    private List<String> createPluginStrings() {
        final List<String> formatList = new ArrayList<String>();
        for (final Plugin plugin : overriddenParameters.getPlugins()) {
            formatList.add(plugin.asPluginString(fileCounter));
        }
        return formatList;
    }

    private static final class EscapeJavaReference implements ReferenceInsertionEventHandler {

        public Object referenceInsert(final String reference, final Object value) {
            if (value == null) {
                return null;
            } else {
                return StringEscapeUtils.escapeJava(value.toString());
            }
        }
    }

}
