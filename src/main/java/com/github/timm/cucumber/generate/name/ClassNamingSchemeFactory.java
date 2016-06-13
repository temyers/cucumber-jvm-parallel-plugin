package com.github.timm.cucumber.generate.name;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Factory for {@link ClassNamingScheme}based on a name.
 *
 * <p>
 * The following naming schemes are supported:
 * </p>
 * <ul>
 * <li>simple</li>
 * <li>feature-title</li>
 * <li>pattern</li>
 * </ul>
 */
public class ClassNamingSchemeFactory {

    private final Counter counter;
    private final ClassNamingScheme featureFileNamingScheme;

    /**
     * @param counter Counter for adding 1-up numbers to generated class names.
     */
    public ClassNamingSchemeFactory(final Counter counter) {
        this.counter = counter;
        featureFileNamingScheme = new FeatureFileClassNamingScheme();
    }

    /**
     * Create a {@link ClassNamingScheme} based on the given name.
     *
     * @param namingScheme The naming scheme to use
     * @param namingPattern Only required if <code>pattern</code> naming scheme is used. The pattern to use when
     *        generating the classname. See {@link PatternNamingScheme} for more details.
     */
    public ClassNamingScheme create(final String namingScheme, final String namingPattern)
                    throws MojoExecutionException {

        if (namingScheme.equals("simple")) {
            return new PatternNamingScheme("Parallel{c}IT", counter, featureFileNamingScheme);
        } else if (namingScheme.equals("feature-title")) {
            return new PatternNamingScheme("{f}{c}IT", counter, featureFileNamingScheme);
            // return new FeatureFileClassNamingScheme(counter);
        } else if (namingScheme.equals("pattern")) {

            if (namingPattern == null) {
                throw new MojoExecutionException("namingPattern tag is required");
            }

            return new PatternNamingScheme(namingPattern, counter, featureFileNamingScheme);
        } else {
            throw new MojoExecutionException("Error in configuration ; accepted value for tag "
                            + "'namingScheme' are 'simple' or 'feature-title' or 'pattern'");
        }

    }

}
