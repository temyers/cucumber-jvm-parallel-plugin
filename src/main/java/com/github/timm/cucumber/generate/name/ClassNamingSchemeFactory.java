package com.github.timm.cucumber.generate.name;

import org.apache.maven.plugin.MojoExecutionException;

public class ClassNamingSchemeFactory {

    private final Counter counter;
    private final ClassNamingScheme featureFileNamingScheme;

    public ClassNamingSchemeFactory(final Counter counter) {
        this.counter = counter;
        featureFileNamingScheme = new FeatureFileClassNamingScheme();
    }

    public ClassNamingScheme create(final String namingScheme, final String namingPattern)
        throws MojoExecutionException {

        if (namingScheme.equals("simple")) {
            return new PatternNamingScheme("Parallel{c}IT", counter, featureFileNamingScheme);
        } else if (namingScheme.equals("feature-title")) {
            return new PatternNamingScheme("{f}{c}IT", counter, featureFileNamingScheme);
            //            return new FeatureFileClassNamingScheme(counter);
        } else if (namingScheme.equals("pattern")) {

            if (namingPattern == null) {
                throw new MojoExecutionException("namingPattern tag is required");
            }

            return new PatternNamingScheme(namingPattern, counter, featureFileNamingScheme);
        } else {
            throw new MojoExecutionException(
                "Error in configuration ; accepted value for tag "
                    + "'namingScheme' are 'simple' or 'feature-title' or 'pattern'");
        }

    }

}
