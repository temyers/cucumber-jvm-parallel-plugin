package com.github.timm.cucumber.generate;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Collection;

public interface CucumberITGenerator {

    /**
     * Generates Cucumber runners.
     * @param outputDirectory the output directory to place generated files
     * @param featureFiles The feature files to create runners for
     * @throws MojoExecutionException if something goes wrong
     */
    void generateCucumberITFiles(final File outputDirectory, final Collection<File> featureFiles)
                    throws MojoExecutionException;
}
