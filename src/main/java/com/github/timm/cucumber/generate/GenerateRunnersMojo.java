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

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import com.github.timm.cucumber.generate.name.OneUpCounter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collection;

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
     * Comma separated list of containing the packages to use for the cucumber glue code. E.g. <code>my.package,
     * my.second.package</code>
     *
     * <P>
     * see cucumber.api.CucumberOptions.glue
     * </P>
     */
    @Parameter(property = "cucumber.glue", required = true)
    private String glue;

    /**
     * Location of the generated files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/cucumber",
                    property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Directory where the cucumber report files shall be written.
     */
    @Parameter(defaultValue = "target/cucumber-parallel", property = "cucumberOutputDir",
                    required = true)
    private String cucumberOutputDir;

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
     * Comma separated list of formats used for the output. Currently only html and json formats are supported.
     *
     * <p>
     * see cucumber.api.CucumberOptions.format
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
     * <p>
     * see cucumber.api.CucumberOptions.tags
     * </p>
     */
    @Parameter(property = "cucumber.tags", required = false)
    private String tags;

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

    private CucumberITGenerator fileGenerator;

    /**
     * Called by Maven to run this mojo after parameters have been injected.
     */
    public void execute() throws MojoExecutionException {

        if (!featuresDirectory.exists()) {
            throw new MojoExecutionException("Features directory does not exist");
        }

        final Collection<File> featureFiles =
                        FileUtils.listFiles(featuresDirectory, new String[] {"feature"}, true);

        createOutputDirIfRequired();

        fileGenerator = createFileGenerator();

        File packageDirectory = packageName == null
                ? outputDirectory
                : new File(outputDirectory, packageName.replace('.','/'));

        if (!packageDirectory.exists()) {
            packageDirectory.mkdirs();
        }

        fileGenerator.generateCucumberITFiles(packageDirectory, featureFiles);

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
    private OverriddenCucumberOptionsParameters overrideParametersWithCucumberOptions() {

        final OverriddenCucumberOptionsParameters overriddenParameters =
                        new OverriddenCucumberOptionsParameters();
        overriddenParameters.setTags(this.tags).setGlue(this.glue).setStrict(this.strict)
                        .setFormat(this.format).setMonochrome(this.monochrome);

        overriddenParameters.overrideParametersWithCucumberOptions(cucumberOptions);

        return overriddenParameters;
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

    public String getCucumberOutputDir() {
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
