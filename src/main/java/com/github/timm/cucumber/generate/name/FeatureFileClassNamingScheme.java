package com.github.timm.cucumber.generate.name;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates a class name based on the feature file filename.
 *
 * <p>
 * The following rules are used to ensure class names are valid:
 * </p>
 * <ul>
 * <li>The file extension is removed.</li>
 * <li>Spaces and '-' are removed, converting to CamelCase.</li>
 * <li>If the filename starts with a digit, the classname is pre-pended with '_'</li>
 * </ul>
 */
public class FeatureFileClassNamingScheme implements ClassNamingScheme {

    private final Pattern startsWithDigit = Pattern.compile("^\\d.*");

    public FeatureFileClassNamingScheme() {}

    /**
     * Generate a class name based on the supplied feature file.
     */
    public String generate(final String featureFileName) {
        String fileNameWithNoExtension = FilenameUtils.removeExtension(featureFileName);

        fileNameWithNoExtension = fileNameWithNoExtension.replaceAll("_", "-");
        fileNameWithNoExtension = fileNameWithNoExtension.replaceAll(" ", "");
        fileNameWithNoExtension = fileNameWithNoExtension.replaceAll("\\.", "-");

        String className =
                        CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, fileNameWithNoExtension);

        final Matcher startsWithDigitCheck = startsWithDigit.matcher(className);

        if (startsWithDigitCheck.matches()) {
            className = "_" + className;
        }

        return className;
    }

}
