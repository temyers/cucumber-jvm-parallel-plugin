package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.TagNamingScheme;
import com.github.timm.cucumber.options.RuntimeOptions;
import com.github.timm.cucumber.options.TagParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class CucumberItGenerator {

    private final FileGeneratorConfig config;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    private final OverriddenRerunOptionsParameters overriddenRerunOptionsParameters;
    private final ClassNamingScheme classNamingScheme;
    int fileCounter = 1;
    private String featureFileLocation;
    private Template velocityTemplate;
    private String outputFileName;
    private String htmlFormat;
    private String jsonFormat;
    private String rerunFormat;

    public CucumberItGenerator(final FileGeneratorConfig config,
                               final OverriddenCucumberOptionsParameters overriddenParameters,
                               final ClassNamingScheme classNamingScheme,
                               final OverriddenRerunOptionsParameters overriddenRerunOptionsPrms) {
        this.config = config;
        this.overriddenParameters = overriddenParameters;
        this.classNamingScheme = classNamingScheme;
        this.overriddenRerunOptionsParameters = overriddenRerunOptionsPrms;
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
            velocityTemplate = engine.getTemplate("cucumber-testng-runner.vm",
                config.getEncoding());
        } else if (config.useReRun()) {
            velocityTemplate = engine.getTemplate("cucumber-junit-re-runner.vm",
                config.getEncoding());
        } else {
            velocityTemplate = engine.getTemplate("cucumber-junit-runner.vm",
                config.getEncoding());
        }
    }

    void generateCucumberItFiles(final File outputDirectory,
                                 final Collection<File> featureFiles,
                                 final RuntimeOptions runtimeOptions)
        throws MojoExecutionException {
        if (config.getNamingScheme().equalsIgnoreCase("tag")) {
            List<String> tags = runtimeOptions.getFilters();
            List<String> parsedTags = new ArrayList<String>();
            for (final String tag : tags) {
                String[] allTags = tag.split(",");
                for (String t : allTags) {
                    parsedTags.add(t);
                }
            }
            for (final String tag : parsedTags) {
                for (final File file : featureFiles) {
                    String fileContents = null;
                    try {
                        final File featuresDirectory = config.getFeaturesDirectory();
                        String FileLocation = featuresDirectory.getPath().replace("features", "")
                            + file.getPath().replace(File.separatorChar, '/');
                        fileContents = FileUtils.readFileToString(new File(FileLocation));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (fileContainsMatchingTags(fileContents, tag)) {
                        System.out.println("File " + file.getName() + " contains tag " + tag);
                        outputFileName = new TagNamingScheme().generate(tag);
                        final File outputFile = new File(outputDirectory, outputFileName);
                        setFeatureFileLocation(file);
                        FileWriter writer = null;
                        try {
                            writer = new FileWriter(outputFile);
                            writeContentFromTemplate(writer, tag);
                        } catch (final IOException e) {
                            throw new MojoExecutionException("Error creating file "
                                + outputFile, e);
                        } finally {
                            if (writer != null) {
                                try {
                                    writer.close();
                                } catch (final IOException e) {
                                    // ignore
                                }
                            }
                        }
                        fileCounter++;
                    }
                }
            }
        } else {
            for (final File file : featureFiles) {

                if (shouldSkipFile(file)) {
                    continue;
                }

                outputFileName = classNamingScheme.generate(file.getName());

                setFeatureFileLocation(file);

                final File outputFile = new File(outputDirectory, outputFileName + ".java");
                FileWriter writer = null;
                try {
                    writer = new FileWriter(outputFile);
                    writeContentFromTemplate(writer);
                } catch (final IOException exception) {
                    throw new MojoExecutionException("Error creating file "
                        + outputFile, exception);
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (final IOException exception) {
                            // ignore
                            System.out.println("Failed to close file: " + outputFile);
                        }
                    }
                }

                fileCounter++;
            }
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

    private boolean fileContainsMatchingTags(final String fileContents, String tag) {

        if (fileContents.contains(tag)) {
            return true;
        }
        return false;
    }


    private boolean fileContainsAnyTags(final String fileContents, final List<String> tags) {

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
     * Sets the feature file location based on the given file. The full file path is trimmed to only
     * include the featuresDirectory. E.g. /myproject/src/test/resources/features/feature1.feature
     * will be saved as features/feature1.feature
     *
     * @param file The feature file
     */
    private void setFeatureFileLocation(final File file) {
        final File featuresDirectory = config.getFeaturesDirectory();
        featureFileLocation = file.getPath()
            .replace(featuresDirectory.getPath(), featuresDirectory.getName())
            .replace(File.separatorChar, '/');
    }

    private void writeContentFromTemplate(final Writer writer) {

        final VelocityContext context = new VelocityContext();
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("reports", createFormatStrings());
        context.put("tags", overriddenParameters.getTags());
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", config.getCucumberOutputDir());
        if (config.useReRun()) {
            context.put("glue", overriddenParameters.getGlue());
        } else {
            context.put("glue", quoteGlueStrings());
        }
        context.put("className", FilenameUtils.removeExtension(outputFileName));
        context.put(
            "outPutPath",
            config.getCucumberOutputDir().replace('\\', '/') + "/"
                + FilenameUtils.removeExtension(outputFileName) + "/"
                + FilenameUtils.removeExtension(outputFileName));
        context.put("retryCount", overriddenRerunOptionsParameters.getRetryCount());
        context.put("htmlFormat", this.htmlFormat);
        context.put("jsonFormat", this.jsonFormat);
        context.put("rerunFormat", this.rerunFormat);
        velocityTemplate.merge(context, writer);
    }

    private void writeContentFromTemplate(final Writer writer, final String tag) {

        final VelocityContext context = new VelocityContext();
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("reports", createFormatStrings());
        context.put("tags", "\"" + tag + "\"");
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", config.getCucumberOutputDir());
        if (config.useReRun()) {
            context.put("glue", overriddenParameters.getGlue());
        } else {
            context.put("glue", quoteGlueStrings());
        }
        context.put("className", FilenameUtils.removeExtension(outputFileName));
        context.put(
            "outPutPath",
            config.getCucumberOutputDir().replace('\\', '/') + "/"
                + FilenameUtils.removeExtension(outputFileName) + "/"
                + FilenameUtils.removeExtension(outputFileName));
        context.put("retryCount", overriddenRerunOptionsParameters.getRetryCount());
        context.put("htmlFormat", this.htmlFormat);
        context.put("jsonFormat", this.jsonFormat);
        context.put("rerunFormat", this.rerunFormat);
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

            if (config.useReRun()) {
                sb.append(String.format("\"%s:%s/%s/%s.%s\"",
                    formatStr,
                    config.getCucumberOutputDir().replace('\\', '/'),
                    FilenameUtils.removeExtension(outputFileName),
                    FilenameUtils.removeExtension(outputFileName),
                    formatStr));
                if (formatStr.equalsIgnoreCase("html")) {
                    htmlFormat = String.format("\"%s:%s/%s/%s.%s\"",
                        formatStr,
                        config.getCucumberOutputDir().replace('\\', '/'),
                        FilenameUtils.removeExtension(outputFileName),
                        FilenameUtils.removeExtension(outputFileName),
                        formatStr);
                }
                if (formatStr.equalsIgnoreCase("json")) {
                    jsonFormat = String.format("\"%s:%s/%s/%s.%s\"",
                        formatStr,
                        config.getCucumberOutputDir().replace('\\', '/'),
                        FilenameUtils.removeExtension(outputFileName),
                        FilenameUtils.removeExtension(outputFileName),
                        formatStr);
                }
                if (formatStr.equalsIgnoreCase("rerun")) {
                    rerunFormat = String.format("\"%s:%s/%s/%s.%s\"",
                        formatStr,
                        config.getCucumberOutputDir().replace('\\', '/'),
                        FilenameUtils.removeExtension(outputFileName),
                        FilenameUtils.removeExtension(outputFileName),
                        "txt");
                }

            } else {
                sb.append(String.format("\"%s:%s/%s.%s\"",
                    formatStr,
                    config.getCucumberOutputDir().replace('\\', '/'),
                    fileCounter,
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
