package com.github.timm.cucumber.generate.name;


public class PatternNamingScheme implements ClassNamingScheme{

    private final String pattern;
    private static int fileCounter = 1;

    public PatternNamingScheme(final String pattern) {

        this.pattern = pattern;
    }

    public String generate(final String featureFileName) {

        final String featureFileClassName = new FeatureFileClassNamingScheme().generate(featureFileName);
        final String className = pattern.replace("{f}", featureFileClassName);
        return className;
    }

}
