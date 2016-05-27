package com.github.timm.cucumber.generate.name;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureFileClassNamingScheme implements ClassNamingScheme {

    private final Counter counter;

    private final Pattern startsWithDigit = Pattern.compile("^\\d.*");

    public FeatureFileClassNamingScheme(final Counter counter) {
        this.counter = counter;
    }

    public String generate(final String featureFileName) {
        String fileNameWithNoExtension = FilenameUtils.removeExtension(featureFileName);

        fileNameWithNoExtension = fileNameWithNoExtension.replaceAll("_", "-");
        fileNameWithNoExtension = fileNameWithNoExtension.replaceAll(" ", "");

        String className =
            CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, fileNameWithNoExtension);

        final Matcher startsWithDigitCheck = startsWithDigit.matcher(className);

        if (startsWithDigitCheck.matches()) {
            className = "_" + className;
        }

        return className;
        //        return String.format(className+"%02dIT.java",counter.next());
    }

}
