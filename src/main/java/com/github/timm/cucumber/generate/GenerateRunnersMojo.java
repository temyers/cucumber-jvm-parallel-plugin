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

/**
 * Goal which generates a Cucumber JUnit runner for each Gherkin feature file in your project
 *
 * @author Tim Myerscough
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
     * Comma separated list of containing the packages to use for the cucumber glue code
     *
     * E.g. <code>my.package, my.second.package</code>
     *
     * @see CucumberOptions.glue
     */
    @Parameter(property = "cucumber.glue", required = true)
    private String glue;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/cucumber", property = "outputDir", required = true)
    private File outputDirectory;

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
     * The format to use for the output. Currently only html and json formats are supported.
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

    private String featureFileLocation;
    private int fileCounter = 1;

    private Template velocityTemplate;

    public void execute() throws MojoExecutionException {

        if (!featuresDirectory.exists()) {
            throw new MojoExecutionException("Features directory does not exist");
        }

        File f = outputDirectory;
        quoteGlueStrings();
        initTemplate();

        Collection<File> featureFiles = FileUtils.listFiles(featuresDirectory, new String[] {"feature"}, true);

        if (!f.exists()) {
            f.mkdirs();
        }

        for (File file : featureFiles) {
            String outputFileName = String.format("Parallel%02dIT.java", fileCounter);

            setFeatureFileLocation(file);

            File outputFile = new File(f, outputFileName);
            FileWriter w = null;
            try {
                w = new FileWriter(outputFile);
                writeContentFromTemplate(w);
            } catch (IOException e) {
                throw new MojoExecutionException("Error creating file " + outputFile, e);
            } finally {
                if (w != null) {
                    try {
                        w.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }

            fileCounter++ ;
        }

        getLog().info("Adding " + outputDirectory.getAbsolutePath() + " to test-compile source root");

        project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    /**
     * Wraps each package in quotes for use in the template
     */
    private void quoteGlueStrings() {
        String[] packageStrs = glue.split(",");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < packageStrs.length; i++ ) {
            String packageStr = packageStrs[i];
            sb.append(String.format("\"%s\"", packageStr.trim()));

            if (i < packageStrs.length - 1) {
                sb.append(", ");
            }
        }
        glue = sb.toString();
    }

    /**
     * Sets the feature file location based on the given file. The full file path is trimmed to only
     * include the featuresDirectory. E.g. /myproject/src/test/resources/features/feature1.feature
     * will be saved as features/feature1.feature
     *
     * @param file
     *            The feature file
     */
    private void setFeatureFileLocation(final File file) {
        featureFileLocation = file.getPath().replace(featuresDirectory.getPath(), featuresDirectory.getName())
                .replace(File.separatorChar, '/');
    }

    private void writeContentFromTemplate(final Writer writer) {

        VelocityContext context = new VelocityContext();
        context.put("strict", strict);
        context.put("featureFile", featureFileLocation);
        String outputFile = String.format("%s.%s", fileCounter, format);
        context.put("outputFile", outputFile);
        context.put("reportType", format);
        context.put("fileCounter", String.format("%02d", fileCounter));
        context.put("tags", tags);
        context.put("monochrome", monochrome);
        context.put("glue", glue);

        velocityTemplate.merge(context, writer);
    }

    private void initTemplate() {
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        velocityTemplate = engine.getTemplate("cucumber-junit-runner.vm", encoding);
    }
}
