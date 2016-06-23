package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;
import gherkin.ast.Scenario;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.Tag;
import org.apache.commons.io.FileUtils;
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

    void generateCucumberItFiles(final File outputDirectory)
        throws MojoExecutionException {

        Collection<File> featureFiles = new ArrayList<File>();
        if (overriddenParameters.getFeaturePaths().size() != 0) {
            for (String f : overriddenParameters.getFeaturePaths()) {
                featureFiles.add(new File(f));
            }
        } else {
            featureFiles = FileUtils.listFiles(config.getFeaturesDirectory(),
                new String[] {"feature"}, true);
        }
        List<String> parsedTags = new ArrayList<String>();
        String[] allTags = overriddenParameters.getTags().split(",");
        for (String t : allTags) {
            parsedTags.add(t.replaceAll("\"", "").replaceAll("\\s+", ""));
        }
        for (final String tag : parsedTags) {
            for (final File file : featureFiles) {
                try {
                    Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
                    GherkinDocument gherkinDocument = null;
                    try {
                        gherkinDocument = parser.parse(new FileReader(file),
                            new TokenMatcher());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    List<Tag> featureTags = gherkinDocument.getFeature().getTags();
                    if (config.filterFeaturesByTags()) {
                        for (Tag t : featureTags) {
                            if (tag.startsWith("~") && ("~" + t.getName()).equalsIgnoreCase(tag)) {
                                // Don't generate file
                            }
                            if (tag.equalsIgnoreCase(t.getName())) {
                                generateItFiles(tag, file, outputDirectory);
                            }
                        }
                    } else {
                        List<ScenarioDefinition> definitions = gherkinDocument.getFeature()
                            .getChildren();
                        for (ScenarioDefinition definition : definitions) {
                            if (definition instanceof ScenarioOutline) {
                                ScenarioOutline scenarioOutline = (ScenarioOutline) definition;
                                List<Tag> outlineTags = scenarioOutline.getTags();
                                for (Tag t : outlineTags) {
                                    if (tag.startsWith("~") && ("~" + t.getName())
                                        .equalsIgnoreCase(tag)) {
                                        // Don't generate file
                                    }
                                    if (tag.equalsIgnoreCase(t.getName())) {
                                        generateItFiles(tag, file, outputDirectory);
                                    }
                                }
                            }
                            if (definition instanceof Scenario) {
                                Scenario scenario = (Scenario) definition;
                                List<Tag> scenarioTags = scenario.getTags();
                                for (Tag t : scenarioTags) {
                                    if (tag.startsWith("~") && ("~" + t.getName())
                                        .equalsIgnoreCase(tag)) {
                                        // Don't generate file
                                    }
                                    if (tag.equalsIgnoreCase(t.getName())) {
                                        generateItFiles(tag, file, outputDirectory);
                                    }
                                }
                            }
                        }
                    }

                } catch (final Exception e) {
                    config.getLog().info(
                        "Failed to read contents of " + file.getPath()
                            + ". Parallel Test shall be created.");
                }
            }
        }

    }

    private void generateItFiles(final String tag, final File file, final File outputDirectory)
        throws MojoExecutionException {
        outputFileName = classNamingScheme.generate(file.getName());
        final File outputFile = new File(outputDirectory, outputFileName + ".java");
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
