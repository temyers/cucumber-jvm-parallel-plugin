package com.github.timm.cucumber.generate.name;

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
 * <li>'{c}' - A one-up number, formatted to have minimum 2 character width.</li>
 * </ul>
 */
public class PatternNamingScheme implements ClassNamingScheme {

    private final String pattern;
    private final Counter counter;
    private final ClassNamingScheme featureFileNamingScheme;

    /**
     * @param pattern The pattern to use.
     * @param counter Counter to generate one up numbers
     * @param featureFileNamingScheme Naming scheme to use for '{f}' placeholder
     */
    public PatternNamingScheme(final String pattern, final Counter counter,
                    final ClassNamingScheme featureFileNamingScheme) {

        this.pattern = pattern;
        this.counter = counter;
        this.featureFileNamingScheme = featureFileNamingScheme;
    }

    /**
     * @param featureFileName The feature file to generate a class name for
     * @return A class name based on the required pattern.
     */
    public String generate(final String featureFileName) {

        String className =
                        pattern.replace("{f}", featureFileNamingScheme.generate(featureFileName));
        className = className.replace("{c}", String.format("%02d", counter.next()));
        return className;
    }

}
