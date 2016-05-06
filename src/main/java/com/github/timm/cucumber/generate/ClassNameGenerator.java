package com.github.temyers.generate;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cwr.sugad.mankar on 5/4/2016.
 */
public class ClassNameGenerator {
    Pattern startsWithDigit = Pattern.compile("^\\d.*");

    public String generateClassNameFromFeatureFileName(String featureFileName, int fileCounter) {

        String fileNameWithNoExtension= FilenameUtils.removeExtension(featureFileName);

        fileNameWithNoExtension=fileNameWithNoExtension.replaceAll("_","-");
        fileNameWithNoExtension=fileNameWithNoExtension.replaceAll(" ","");

        String className = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, fileNameWithNoExtension);

        Matcher startsWithDigitCheck = startsWithDigit.matcher(className);

        if(startsWithDigitCheck.matches()){
            className="_"+className;
        }

        return String.format(className+"%02dIT.java",fileCounter);
    }

    public String generateSimpleClassName(int fileCounter) {

        return String.format("Parallel%02dIT.java",fileCounter);

    }
}
