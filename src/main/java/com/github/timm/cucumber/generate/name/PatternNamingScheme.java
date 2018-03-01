package com.github.timm.cucumber.generate.name;

import com.github.timm.cucumber.ModuloCounter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate a Class Name based on a pattern.
 *
 * <p>
 * No validation is performed to ensure that the generated class name is valid.
 * </p>
 *
 * <p>
 * The following placeholders are supported:
 * </p>
 * <ul>
 * <li>'{f}' - The feature file, converted using supplied naming scheem.</li>
 * <li>'{c}' - A one-up number, formatted to have minimum 2 character width
 * , but optionally prefix-able by width required. Example {3c} resulting 001,002....</li>
 * <li>'{c:n}' - A one-up number modulo n, with no minimum character width
 * , but optionally prefix-able by width required. Example {2c:16} resulting 00,01,02,....,15</li>
 * </ul>
 */
public class PatternNamingScheme implements ClassNamingScheme {

    private static final Pattern COUNTER_PATTERN = Pattern.compile("\\{(\\d*)c}");
    private static final Pattern MODULO_COUNTER_PATTERN = Pattern.compile("\\{(\\d*)c:(\\d+)}");

    private final String pattern;
    private final Counter counter;
    private final Counter moduloCounter;
    private final ClassNamingScheme featureFileNamingScheme;

    /**
     * Constructor.
     * @param pattern The pattern to use.
     * @param counter Counter to generate one up numbers
     * @param featureFileNamingScheme Naming scheme to use for '{f}' placeholder
     */
    public PatternNamingScheme(final String pattern, final Counter counter,
                    final ClassNamingScheme featureFileNamingScheme) {

        this.pattern = pattern;
        this.counter = counter;
        this.moduloCounter = new ModuloCounter(pattern);
        this.featureFileNamingScheme = featureFileNamingScheme;
    }

    /**
     * Generate a class name using the required pattern and feature file.
     * @param featureFileName The feature file to generate a class name for
     * @return A class name based on the required pattern.
     */
    public String generate(final String featureFileName) {

        String className =
                        pattern.replace("{f}", featureFileNamingScheme.generate(featureFileName));

        int number = counter.next();
        className = replaceAll( COUNTER_PATTERN, className, number);
        className = replaceAll( MODULO_COUNTER_PATTERN, className, number);
        return className;
    }

    private String replaceAll(Pattern compiledPattern,String pattern, int number) {
        Matcher matcher = compiledPattern.matcher(pattern);
        boolean isNormalCounter = compiledPattern == COUNTER_PATTERN;
        int defaultLen = isNormalCounter ? 2 : 1;

        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                boolean isNoLength = matcher.start(1) == matcher.end(1);
                int len = isNoLength ? defaultLen : Integer.decode(matcher.group(1));
                int numberAdjustedIfMod = isNormalCounter ? number : (number - 1) % Integer.decode(matcher.group(2));
                matcher.appendReplacement(sb, String.format("%0" + len + "d", numberAdjustedIfMod));
                result = matcher.find();
            }
            while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return pattern;
    }

}
