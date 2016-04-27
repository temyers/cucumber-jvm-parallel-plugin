package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.name.FeatureFileClassNamingScheme;
import com.github.timm.cucumber.generate.name.SimpleClassNamingScheme;

public class ClassNameGenerator {

    private final SimpleClassNamingScheme simpleGenerator = new SimpleClassNamingScheme();

    public String generateClassNameFromFeatureFileName(final String featureFileName, final int fileCounter) {

        return new FeatureFileClassNamingScheme().generate(featureFileName);
    }

    /**
     * @deprecated Use {@link #generateSimpleClassName()} instead
     */
    @Deprecated
    public String generateSimpleClassName(final int fileCounter) {
        return generateSimpleClassName();
    }

    public String generateSimpleClassName() {

        //        return String.format("Parallel%02dIT.java",fileCounter);
        return simpleGenerator.generate(null);
    }

}
