package com.github.timm.cucumber.generate.name;


public class PatternNamingScheme implements ClassNamingScheme{

    private final String pattern;
    private final Counter counter;

    public PatternNamingScheme(final String pattern, final Counter counter) {

        this.pattern = pattern;
        this.counter = counter;
    }

    public String generate(final String featureFileName) {

        final String featureFileClassName = new FeatureFileClassNamingScheme(counter).generate(featureFileName);
        String className = pattern.replace("{f}", featureFileClassName);
        className = className.replace("{c}",String.format("%02d", counter.next()));
        return className;
    }

}
