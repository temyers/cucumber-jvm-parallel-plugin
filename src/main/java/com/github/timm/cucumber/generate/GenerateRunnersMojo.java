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

import static com.github.timm.cucumber.generate.Plugin.createBuildInPlugin;
import static org.apache.commons.io.FileUtils.listFiles;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import com.github.timm.cucumber.generate.name.OneUpCounter;
import com.github.timm.cucumber.options.RuntimeOptions;
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
     * List of packages to use for the cucumber glue code. E.g. <pre>{@code
     *     <glue>
     *         <package>com.example</package>
     *         <package>com.example.other</package>
     *     </glue>
     * }</pre>
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
     * Default is json.<pre>{@code
     * <format>html,json</format>
     * }</pre>
     *
     * <p>
     * see cucumber.api.CucumberOptions.plugin
     * </p>
     */
    @Parameter(defaultValue = "json", property = "cucumber.format", required = true)
    private String format;

    /**
     * List of cucumber plugins used . E.g. <pre>{@code
     * <plugins>
     *    <plugin>
     *      <name>path.to.my.formaters.CustomHtmlFormatter</name>
     *      <extension>html</extension>
     *    </plugin>
     *    <plugin>
     *      <name>path.to.my.formaters.CustomJsonFormatter</name>
     *      <extension>json</extension>
     *      <outputDirectory>${project.build.directory}/cucumber-parallel/custom-json/</outputDirectory>
     *    </plugin>
     * </plugins>
     * }</pre>
     * <p> see cucumber.api.CucumberOptions.plugins </p>
     */
    @Parameter
    private List<Plugin> plugins;

    /**
     * <p>
     * see cucumber.api.CucumberOptions.monochrome
     * </p>
     */
    @Parameter(defaultValue = "false", property = "cucumber.monochrome", required = true)
    private boolean monochrome;

    /**
     * List of tags used to select scenarios to run. E.g. <pre>{@code
     *     <tags>
     *         <tag>@billing</tag>
     *         <tag>~@billing</tag>
     *         <tag>@important</tag>
     *         <tag>@important,@billing</tag>
     *     </tags>
     * }</pre>
     *
     * <p>
     * see cucumber.api.CucumberOptions.tags
     * </p>
     */
    @Parameter(property = "cucumber.tags", required = false)
    private List<String> tags;

    @Parameter(defaultValue = "UTF-8", property = "project.build.sourceEncoding", readonly = true)
    private String encoding;

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
     * <li><pre>{@code FEATURE}</pre> -Generate one runner per feature</li>
     * <li><pre>{@code SCENARIO}</pre> - Generate one runner per scenario. A runner shall be created
     * for each example of a scenario outline</li>
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
     * Overrides the parameters with cucumber.options if they have been specified. Plugins have
     * somewhat limited support.
     */
    private OverriddenCucumberOptionsParameters overrideParametersWithCucumberOptions() throws MojoExecutionException {

        try {
            final OverriddenCucumberOptionsParameters overriddenParameters =
                new OverriddenCucumberOptionsParameters()
                    .setTags(tags)
                    .setGlue(glue)
                    .setStrict(strict)
                    .setPlugins(parseFormatAndPlugins(format, plugins == null ? new ArrayList<Plugin>() : plugins))
                    .setMonochrome(monochrome);


            if (cucumberOptions != null && cucumberOptions.length() > 0) {
                final RuntimeOptions options = new RuntimeOptions(cucumberOptions);
                overriddenParameters
                    .overrideTags(options.getFilters())
                    .overrideGlue(options.getGlue())
                    .overridePlugins(parsePlugins(options.getPluginNames()))
                    .overrideStrict(options.isStrict())
                    .overrideMonochrome(options.isMonochrome());
            }

            return overriddenParameters;
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException(this, "Invalid parameter. ", e.getMessage());
        }
    }


    private List<Plugin> parsePlugins(List<String> pluginStrings) throws MojoExecutionException {
        final List<Plugin> plugins = new ArrayList<Plugin>();
        for (String pluginString : pluginStrings) {
            try {
                plugins.add(createBuildInPlugin(pluginString));
            } catch (IllegalArgumentException e) {
                throw new MojoExecutionException(this,
                    "Invalid plugin in cucumber.options",
                    "Cucumber options only supports build in plugins. "
                    + "'" + pluginString + "' is not supported as a commandline option, "
                    + "try it as a plugin in the pom?");
            }
        }

        for (Plugin plugin : plugins) {
            plugin.applyDefaults(cucumberOutputDir);
        }

        return plugins;
    }

    private List<Plugin> parseFormatAndPlugins(String formats, List<Plugin> plugins) throws MojoExecutionException {
        if (!"json".equals(formats)) {
            getLog().warn("The format parameter is deprecated. Please use plugins");

            for (String format : formats.split(",")) {
                try {
                    plugins.add(createBuildInPlugin(format));
                } catch (IllegalArgumentException e) {
                    throw new MojoExecutionException("Format '" + format + "' is not supported as a "
                        + "format, try it as a plugin?", e);
                }
            }
        }

        // Add the default plugin if no others were provided through the format or plugins property.
        if (plugins.isEmpty()) {
            plugins.add(createBuildInPlugin("json"));
        }

        for (Plugin plugin : plugins) {
            plugin.applyDefaults(cucumberOutputDir);
        }

        return plugins;
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
