package com.github.timm.cucumber.generate.name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.CaseFormat;

public class FeatureFileClassNamingScheme implements ClassNamingScheme{


    private final Pattern startsWithDigit = Pattern.compile("^\\d.*");
    private static int fileCounter = 1;

    public String generate(final String featureFileName) {
        String fileNameWithNoExtension= FilenameUtils.removeExtension(featureFileName);

        fileNameWithNoExtension=fileNameWithNoExtension.replaceAll("_","-");
        fileNameWithNoExtension=fileNameWithNoExtension.replaceAll(" ","");

        String className = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, fileNameWithNoExtension);

        final Matcher startsWithDigitCheck = startsWithDigit.matcher(className);

        if(startsWithDigitCheck.matches()){
            className="_"+className;
        }

        return String.format(className+"%02dIT.java",fileCounter++);
    }

}
