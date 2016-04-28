package com.github.timm.cucumber.generate.name;

import org.apache.maven.plugin.MojoExecutionException;

public class ClassNamingSchemeFactory {

    public static ClassNamingScheme create(final String namingScheme, final String namingPattern) throws MojoExecutionException {

        if(namingScheme.equals("simple")){
            return new SimpleClassNamingScheme();
        }
        else if (namingScheme.equals("feature-title")) {
            return new FeatureFileClassNamingScheme();
        } else if (namingScheme.equals("pattern")) {

            if(namingPattern == null) {
                throw new MojoExecutionException("namingPattern tag is required");
            }

            return new PatternNamingScheme(namingPattern);
        }
        else {
            throw new MojoExecutionException("Error in configuration ; accepted value for tag 'namingScheme' are 'simple' or 'feature-title' or 'pattern'");
        }

    }

}
