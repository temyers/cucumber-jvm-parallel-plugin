package com.github.timm.cucumber.generate;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.listFiles;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import com.github.timm.cucumber.generate.name.OneUpCounter;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Goal which generates a Cucumber JUnit runner for each Gherkin feature file in your project.
 */
@Mojo(name = "generateRunners", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class GenerateRunnersMojo extends AbstractMojo implements FileGeneratorConfig {

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
     * List of packages to use for the cucumber glue code. E.g. <code>
     *     <glue>
     *         <package>my.package</package>
     *         <package>my.other.package</package>
     *     </glue>
     * </code>
     *
     * <p>
     * see cucumber.api.CucumberOptions.glue
     * </p>
     */
    @Parameter(property = "cucumber.glue", required = true)
    private List<String> glue;

    /**
     * Location of the generated files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/cucumber",
                    property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Directory where the cucumber report files shall be written.
     */
    @Parameter(defaultValue = "${project.build.directory}/cucumber-parallel", property = "cucumberOutputDir",
            required = true)
    private File cucumberOutputDir;

    /**
     * Directory containing the feature files.
     */
    @Parameter(defaultValue = "src/test/resources/features/", property = "featuresDir",
                    required = true)
    private File featuresDirectory;

    /**
     * see cucumber.api.CucumberOptions.strict
     */
    @Parameter(defaultValue = "true", property = "cucumber.strict", required = true)
    private boolean strict;

    /**
     * Comma separated list of format used for the output. Currently only html, json and pretty format are supported.
     * Default is json.<code>
     * <format>html,json</format>
     * </code>
     *
     * <p>
     * see cucumber.api.CucumberOptions.plugin
     * </p>
     */
    @Parameter(defaultValue = "json", property = "cucumber.format", required = true)
    private String format;

    /**
     * <p>
     * see cucumber.api.CucumberOptions.monochrome
     * </p>
     */
    @Parameter(defaultValue = "false", property = "cucumber.monochrome", required = true)
    private boolean monochrome;

    /**
     * List of tags used to select scenarios to run. E.g. <code>
     *     <tags>
     *         <tag>@billing</tag>
     *         <tag>~@billing</tag>
     *         <tag>@important</tag>
     *         <tag>@important,@billing</tag>
     *     </tags>
     * </code>
     *
     * <p>
     * see cucumber.api.CucumberOptions.tags
     * </p>
     */
    @Parameter(property = "cucumber.tags", required = false)
    private List<String> tags;

    @Parameter(defaultValue = "UTF-8", property = "project.build.sourceEncoding", readonly = true)
    private String encoding;

    @Parameter(defaultValue = "false", property = "cucumber.tags.filterOutput", required = true)
    private boolean filterFeaturesByTags;

    @Parameter(defaultValue = "false", property = "useTestNG", required = true)
    private boolean useTestNG;

    /**
     * see cucumber.api.CucumberOptions
     */
    @Parameter(property = "cucumber.options", required = false)
    private String cucumberOptions;

    @Parameter(defaultValue = "simple", property = "namingScheme", required = false)
    private String namingScheme;

    @Parameter(property = "namingPattern", required = false)
    private String namingPattern;

    @Parameter(property = "packageName", required = false)
    private String packageName;

    /**
     * The scheme to use when generating runner. Valid values are:
     * <ul>
     * <li><code>FEATURE</code> - Generate one runner per feature</li>
     * <li><code>SCENARIO</code> - Generate one runner per scenario. A runner shall be created for each example of a
     * scenario outline</li>
     * </ul>
     * @see ParallelScheme
     */
    @Parameter(defaultValue = "FEATURE", property = "parallelScheme", required = true)
    private ParallelScheme parallelScheme;

    @Parameter(property = "customVmTemplate", required = false)
    private String customVmTemplate;

    /**
     * Called by Maven to run this mojo after parameters have been injected.
     */
    public void execute() throws MojoExecutionException {

        if (!featuresDirectory.exists()) {
            throw new MojoExecutionException("Features directory does not exist");
        }

        final Collection<File> featureFiles = listFiles(featuresDirectory, new String[] {"feature"}, true);
        final List<File> sortedFeatureFiles = new NameFileComparator().sort(new ArrayList<File>(featureFiles));

        createOutputDirIfRequired();

        File packageDirectory = packageName == null
                ? outputDirectory
                : new File(outputDirectory, packageName.replace('.','/'));

        if (!packageDirectory.exists()) {
            packageDirectory.mkdirs();
        }

        final CucumberITGenerator fileGenerator = createFileGenerator();
        fileGenerator.generateCucumberITFiles(packageDirectory, sortedFeatureFiles);

        getLog().info("Adding " + outputDirectory.getAbsolutePath()
                        + " to test-compile source root");

        project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    private CucumberITGenerator createFileGenerator() throws MojoExecutionException {
        final OverriddenCucumberOptionsParameters overriddenParameters =
                        overrideParametersWithCucumberOptions();
        final ClassNamingSchemeFactory factory = new ClassNamingSchemeFactory(new OneUpCounter());
        final ClassNamingScheme classNamingScheme = factory.create(namingScheme, namingPattern);

        return new CucumberITGeneratorFactory(this, overriddenParameters, classNamingScheme)
                        .create(parallelScheme);
    }

    private void createOutputDirIfRequired() {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
    }

    /**
     * Overrides the parameters with cucumber.options if they have been specified. Currently only tags are supported.
     */
    private OverriddenCucumberOptionsParameters overrideParametersWithCucumberOptions() throws MojoExecutionException {

        final List<String> cleanPlugins = new ArrayList<String>();
        for (String plugin : this.format.split(",")) {
            cleanPlugins.add(plugin.trim());
        }

        try {
            final OverriddenCucumberOptionsParameters overriddenParameters =
                    new OverriddenCucumberOptionsParameters()
                            .setTags(this.tags)
                            .setGlue(this.glue)
                            .setStrict(this.strict)
                            .setPlugins(cleanPlugins)
                            .setMonochrome(this.monochrome);

            overriddenParameters.overrideParametersWithCucumberOptions(cucumberOptions);
            return overriddenParameters;
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException(e, e.getMessage(), e.getMessage());
        }
    }

    public boolean filterFeaturesByTags() {
        return filterFeaturesByTags;
    }

    public File getFeaturesDirectory() {
        return featuresDirectory;
    }

    public String getEncoding() {
        return encoding;
    }

    public File getCucumberOutputDir() {
        return cucumberOutputDir;
    }

    public boolean useTestNG() {
        return useTestNG;
    }

    public String getNamingScheme() {
        return namingScheme;
    }

    public String getNamingPattern() {
        return namingPattern;
    }

    public String getCustomVmTemplate() {
        return customVmTemplate;
    }

    public String getPackageName() {
        return packageName;
    }

    public File getProjectBasedir() {
        return project.getBasedir();
    }

}
