package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import org.apache.maven.plugin.MojoExecutionException;

public class CucumberITGeneratorFactory {

    private final OverriddenCucumberOptionsParameters overriddenParameters;
    private final ClassNamingScheme classNamingScheme;
    private final FileGeneratorConfig config;

    /**
     * @param config generator config.
     * @param overriddenParameters cucumber options params
     * @param classNamingScheme the class naming scheme to use
     */
    public CucumberITGeneratorFactory(final FileGeneratorConfig config,
                    final OverriddenCucumberOptionsParameters overriddenParameters,
                    final ClassNamingScheme classNamingScheme) {
        this.overriddenParameters = overriddenParameters;
        this.classNamingScheme = classNamingScheme;
        this.config = config;
    }

    /**
     * Create a CucumberITGenerator based on the given parallel scheme.
     * @param parallelScheme The scheme to use
     * @return
     * @throws MojoExecutionException
     */
    public CucumberITGenerator create(final ParallelScheme parallelScheme) throws MojoExecutionException {
        if (ParallelScheme.FEATURE.equals(parallelScheme)) {

            return createFileGeneratorByFeature();
        } else {
            return createFileGeneratorByScenario();
        }
    }

    private CucumberITGenerator createFileGeneratorByFeature() throws MojoExecutionException {
        return new CucumberITGeneratorByFeature(config, overriddenParameters, classNamingScheme);
    }

    private CucumberITGenerator createFileGeneratorByScenario() throws MojoExecutionException {
        return new CucumberITGeneratorByScenario(config, overriddenParameters, classNamingScheme);
    }
}
