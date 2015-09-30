package com.github.timm.cucumber.generate;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.github.timm.cucumber.options.TagParser;

/**
 * Goal which generates a Cucumber JUnit runner for each Gherkin feature file in
 * your project
 *
 *
 */
@Mojo(name = "generateRunners", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class GenerateRunnersMojo extends AbstractMojo {

    /**
     * The current project representation.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    /**
     * Comma separated list of containing the packages to use for the cucumber
     * glue code
     *
     * E.g. <code>my.package, my.second.package</code>
     *
     * @see CucumberOptions.glue
     */
    @Parameter(property = "cucumber.glue", required = true)
    private String glue;

    /**
     * Location of the generated files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/cucumber", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Directory where the cucumber report files shall be written.
     */
    @Parameter(defaultValue = "target/cucumber-parallel", property = "cucumberOutputDir", required = true)
    private String cucumberOutputDir;

    /**
     * Directory containing the feature files
     */
    @Parameter(defaultValue = "src/test/resources/features/", property = "featuresDir", required = true)
    private File featuresDirectory;

    /**
     * @see CucumberOptions.strict
     */
    @Parameter(defaultValue = "true", property = "cucumber.strict", required = true)
    private boolean strict;

    /**
     * Comma separated list of formats used for the output. Currently only html
     * and json formats are supported.
     *
     * @see CucumberOptions.format
     */
    @Parameter(defaultValue = "json", property = "cucumber.format", required = true)
    private String format;

    /**
     * @see CucumberOptions.monochrome
     */
    @Parameter(defaultValue = "false", property = "cucumber.monochrome", required = true)
    private boolean monochrome;

    /**
     * @see CucumberOptions.tags
     */
    @Parameter(defaultValue = "\"@complete\", \"@accepted\"", property = "cucumber.tags", required = true)
    private String tags;

    @Parameter(defaultValue = "UTF-8", property = "project.build.sourceEncoding", readonly = true)
    private String encoding;

    @Parameter(defaultValue = "false", property = "cucumber.tags.filterOutput", required = true)
    private boolean filterFeaturesByTags;

    /**
     * @see CucumberOptions
     */
    @Parameter(property = "cucumber.options", required = false)
    private String cucumberOptions;

    private String featureFileLocation;
    private int fileCounter = 1;

    private Template velocityTemplate;

    private OverriddenCucumberOptionsParameters overriddenParameters;

    public void execute() throws MojoExecutionException {

        overrideParametersWithCucumberOptions();

        if (!featuresDirectory.exists()) {
            throw new MojoExecutionException(
                    "Features directory does not exist");
        }

        final File f = outputDirectory;
        initTemplate();

        final Collection<File> featureFiles = FileUtils.listFiles(
                featuresDirectory, new String[] { "feature" }, true);

        createOutputDirIfRequired(f);

        for (final File file : featureFiles) {

            if (shouldSkipFile(file)) {
                continue;
            }

            final String outputFileName = String.format("Parallel%02dIT.java",
                    fileCounter);

            setFeatureFileLocation(file);

            final File outputFile = new File(f, outputFileName);
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

        getLog().info(
                "Adding " + outputDirectory.getAbsolutePath()
                + " to test-compile source root");

        project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    private void createOutputDirIfRequired(final File f) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    private boolean shouldSkipFile(final File file) {
        if (filterFeaturesByTags) {
            try {
                final String fileContents = FileUtils.readFileToString(file);

                if (!fileContainsMatchingTags(fileContents)) {
                    return true;
                }
            } catch (final IOException e) {
                getLog().info(
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
     * Create the format string used for the output.
     */
    private String createFormatStrings() {
        final String[] formatStrs = overriddenParameters.getFormat().split(",");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < formatStrs.length; i++) {
            final String formatStr = formatStrs[i].trim();
            sb.append(String.format("\"%s:%s/%s.%s\"", formatStr,
                    cucumberOutputDir, fileCounter, formatStr));

            if (i < formatStrs.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
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
        context.put("cucumberOutputDir", cucumberOutputDir);
        context.put("glue", quoteGlueStrings());

        velocityTemplate.merge(context, writer);
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

    private void initTemplate() {
        final Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        velocityTemplate = engine.getTemplate("cucumber-junit-runner.vm",
                encoding);
    }

    /**
     * Overrides the parameters with cucumber.options if they have been
     * specified. Currently only tags are supported.
     */
    // Default for testing
    void overrideParametersWithCucumberOptions() {

        overriddenParameters = new OverriddenCucumberOptionsParameters();
        overriddenParameters.setTags(this.tags).setGlue(this.glue)
        .setStrict(this.strict).setFormat(this.format)
        .setMonochrome(this.monochrome);

        overriddenParameters
        .overrideParametersWithCucumberOptions(cucumberOptions);

    }

    // These setters are provided for testing only.
    final void setGlue(final String glue) {
        this.glue = glue;
    }

    final void setStrict(final boolean strict) {
        this.strict = strict;
    }

    final void setFormat(final String format) {
        this.format = format;
    }

    final void setMonochrome(final boolean monochrome) {
        this.monochrome = monochrome;
    }

    final void setTags(final String tags) {
        this.tags = tags;
    }

    final void setCucumberOptions(final String cucumberOptions) {
        this.cucumberOptions = cucumberOptions;
    }

    final String getGlue() {
        return overriddenParameters.getGlue();
    }

    final boolean isStrict() {
        return overriddenParameters.isStrict();
    }

    final String getFormat() {
        return overriddenParameters.getFormat();
    }

    final boolean isMonochrome() {
        return overriddenParameters.isMonochrome();
    }

    final String getTags() {
        return overriddenParameters.getTags();
    }

}
