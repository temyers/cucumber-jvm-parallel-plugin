package com.github.timm.cucumber.generate.name;

public class SimpleClassNamingScheme implements ClassNamingScheme {

    private static int fileCounter = 1;

    public String generate(final String featureFileName) {
        return String.format("Parallel%02dIT.java", fileCounter++);
    }
}
