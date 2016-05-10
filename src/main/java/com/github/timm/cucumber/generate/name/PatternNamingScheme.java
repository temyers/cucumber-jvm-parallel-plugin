package com.github.timm.cucumber.generate.name;


public class PatternNamingScheme implements ClassNamingScheme{

    private final String pattern;
    private final Counter counter;
    private final ClassNamingScheme featureFileNamingScheme;

    public PatternNamingScheme(final String pattern, final Counter counter, final ClassNamingScheme featureFileNamingScheme) {

        this.pattern = pattern;
        this.counter = counter;
        this.featureFileNamingScheme = featureFileNamingScheme;
    }

    public String generate(final String featureFileName) {

        String className = pattern.replace("{f}", featureFileNamingScheme.generate(featureFileName));
        className = className.replace("{c}",String.format("%02d", counter.next()));
        return className;
    }

}
