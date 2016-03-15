package com.github.timm.cucumber.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.github.timm.cucumber.options.TagParser;

public class CucumberITGenerator {

    private final FileGeneratorConfig config;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    int fileCounter = 1;
    private String featureFileLocation;
    private Template velocityTemplate;

    public CucumberITGenerator(final FileGeneratorConfig config, final OverriddenCucumberOptionsParameters overriddenParameters) {
        this.config = config;
        this.overriddenParameters = overriddenParameters;
        initTemplate();
    }

    private void initTemplate() {
        final Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        if (config.useTestNG()){
            velocityTemplate = engine.getTemplate("cucumber-testng-runner.vm",
                                                  config.getEncoding());
        } else {
            velocityTemplate = engine.getTemplate("cucumber-junit-runner.vm",
                                                  config.getEncoding());
        }
    }

    void generateCucumberITFiles(final File outputDirectory, final Collection<File> featureFiles)
            throws MojoExecutionException {
        for (final File file : featureFiles) {

            if (shouldSkipFile(file)) {
                continue;
            }

            final String outputFileName = String.format("Parallel%02dIT.java",
                    fileCounter);

            setFeatureFileLocation(file);

            final File outputFile = new File(outputDirectory, outputFileName);
            FileWriter w = null;
            try {
                w = new FileWriter(outputFile);
                writeContentFromTemplate(w);
            } catch (final IOException e) {
                throw new MojoExecutionException("Error creating file "
                        + outputFile, e);
            } finally {
                if (w != null) {
                    try {
                        w.close();
                    } catch (final IOException e) {
                        // ignore
                    }
                }
            }

            fileCounter++;
        }
    }

    private boolean shouldSkipFile(final File file) {
        if (config.filterFeaturesByTags()) {
            try {
                final String fileContents = FileUtils.readFileToString(file);

                if (!fileContainsMatchingTags(fileContents)) {
                    return true;
                }
            } catch (final IOException e) {
                config.getLog().info(
                        "Failed to read contents of " + file.getPath()
                        + ". Parallel Test shall be created.");
            }
        }
        return false;
    }

    private boolean fileContainsMatchingTags(final String fileContents) {

        final List<List<String>> tagGroupsAnded = TagParser
                .splitQuotedTagsIntoParts(overriddenParameters.getTags());

        // Tag groups are and'd together
        for (final List<String> tagGroup : tagGroupsAnded) {
            // individual tags are or'd together
            if (!fileContainsAnyTags(fileContents, tagGroup)) {
                return false;
            }
        }

        return true;
    }

    private boolean fileContainsAnyTags(final String fileContents,
            final List<String> tags) {

        for (final String tag : tags) {

            if (tag.startsWith("~")) {
                // not tags must be ignored - cannot guarantee that a feature
                // file containing an ignored tag does not contain scenarios
                // that
                // should be included
                return true;
            }

            if (fileContents.contains(tag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sets the feature file location based on the given file. The full file
     * path is trimmed to only include the featuresDirectory. E.g.
     * /myproject/src/test/resources/features/feature1.feature will be saved as
     * features/feature1.feature
     *
     * @param file
     *            The feature file
     */
    private void setFeatureFileLocation(final File file) {
        final File featuresDirectory = config.getFeaturesDirectory();
        featureFileLocation = file
                .getPath()
                .replace(featuresDirectory.getPath(),
                        featuresDirectory.getName())
                        .replace(File.separatorChar, '/');
    }

    private void writeContentFromTemplate(final Writer writer) {

        final VelocityContext context = new VelocityContext();
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("reports", createFormatStrings());
        context.put("fileCounter", String.format("%02d", fileCounter));
        context.put("tags", overriddenParameters.getTags());
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", config.getCucumberOutputDir());
        context.put("glue", quoteGlueStrings());

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
                    config.getCucumberOutputDir()
                    .replace('\\', '/'), fileCounter, formatStr));

            if (i < formatStrs.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Wraps each package in quotes for use in the template
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
