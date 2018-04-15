package com.github.timm.cucumber.generate;

import static java.lang.String.format;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.runtime.TagPredicate;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.Feature;
import gherkin.ast.GherkinDocument;
import gherkin.pickles.Compiler;
import gherkin.pickles.Pickle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Generates Cucumber runner files using configuration from FileGeneratorConfig containing parameters passed into the
 * Maven Plugin configuration.
 *
 * @deprecated Generating runners by feature is deprecated, creating runners per scenario is preferred. This class shall
 *             be removed in a future version.
 */
@Deprecated
public class CucumberITGeneratorByFeature implements CucumberITGenerator {

    private final FileGeneratorConfig config;
    private final OverriddenCucumberOptionsParameters overriddenParameters;
    private int fileCounter = 1;
    private String featureFileLocation;
    private Feature parsedFeature;
    private Template velocityTemplate;
    private String outputFileName;
    private final ClassNamingScheme classNamingScheme;


    /**
     * Constructor.
     * @param config               The configuration parameters passed to the Maven Mojo
     * @param overriddenParameters Parameters overridden from Cucumber options VM parameter (-Dcucumber.options)
     * @param classNamingScheme    The naming scheme to use for the generated class files
     */
    public CucumberITGeneratorByFeature(final FileGeneratorConfig config,
                                        final OverriddenCucumberOptionsParameters overriddenParameters,
                                        final ClassNamingScheme classNamingScheme) {
        this.config = config;
        this.overriddenParameters = overriddenParameters;
        this.classNamingScheme = classNamingScheme;
        initTemplate();
    }

    private void initTemplate() {
        final Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        final String name;
        if (StringUtils.isNotBlank(config.getCustomVmTemplate())) {
            // Look for custom template on the classpath or as a relative file path
            props.put("resource.loader", "class, file");
            props.put("file.resource.loader.path", config.getProjectBasedir().getAbsolutePath());
            name = config.getCustomVmTemplate();
        } else if (config.useTestNG()) {
            name = "cucumber-testng-runner.java.vm";
        } else {
            name = "cucumber-junit-runner.java.vm";
        }
        final VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        velocityTemplate = engine.getTemplate(name, config.getEncoding());
    }

    /**
     * Generates a single Cucumber runner for each separate feature file.
     *
     * @param outputDirectory the output directory to place generated files
     * @param featureFiles    The feature files to create runners for
     * @throws MojoExecutionException if something goes wrong
     */
    public void generateCucumberITFiles(final File outputDirectory,
                                        final Collection<File> featureFiles) throws MojoExecutionException {
        Parser<GherkinDocument> parser = new Parser<GherkinDocument>(new AstBuilder());
        TagPredicate tagPredicate = new TagPredicate(overriddenParameters.getTags());

        TokenMatcher matcher = new TokenMatcher();

        for (final File file : featureFiles) {
            GherkinDocument gherkinDocument = null;
            final List<Pickle> acceptedPickles = new ArrayList<Pickle>();
            try {
                String source = FileUtils.readFileToString(file);
                gherkinDocument = parser.parse(source, matcher);
                Compiler compiler = new Compiler();
                List<Pickle> pickles = compiler.compile(gherkinDocument);

                for (Pickle pickle : pickles) {
                    if (tagPredicate.apply(pickle.getTags())) {
                        acceptedPickles.add(pickle);
                        continue;
                    }
                }

            } catch (final IOException e) {
                // should never happen
                // TODO - proper logging
                System.out.println(format("WARNING: Failed to parse '%s'...IGNORING",
                        file.getName()));
            }

            if (acceptedPickles.isEmpty()) {
                continue;
            }

            outputFileName = classNamingScheme.generate(file.getName());
            setFeatureFileLocation(file);
            setParsedFeature(gherkinDocument.getFeature());
            writeFile(outputDirectory);

        }
    }

    private void writeFile(final File outputDirectory) throws MojoExecutionException {
        final File outputFile = new File(outputDirectory, outputFileName + ".java");
        FileWriter w = null;
        try {
            w = new FileWriter(outputFile);
            writeContentFromTemplate(w);
        } catch (final IOException e) {
            throw new MojoExecutionException("Error creating file " + outputFile, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (final IOException e) {
                    // ignore
                    System.out.println("Failed to close file: " + outputFile);
                }
            }
        }

        fileCounter++;
    }

    /**
     * Sets the feature file location based on the given file. The full file path is trimmed to only include the
     * featuresDirectory. E.g. /myproject/src/test/resources/features/feature1.feature will be saved as
     * features/feature1.feature
     *
     * @param file The feature file
     */
    private void setFeatureFileLocation(final File file) {
        featureFileLocation = normalizePathSeparator(file);
    }


    private void setParsedFeature(final Feature feature) {
        parsedFeature = feature;
    }

    private static String normalizePathSeparator(final File file) {
        return file.getPath().replace(File.separatorChar, '/');
    }

    private void writeContentFromTemplate(final Writer writer) {
        // to escape java
        final EventCartridge ec = new EventCartridge();
        ec.addEventHandler(new EscapeJavaReference());

        final VelocityContext context = new VelocityContext();
        context.attachEventCartridge(ec);
        context.put("strict", overriddenParameters.isStrict());
        context.put("featureFile", featureFileLocation);
        context.put("plugins", createPluginStrings());
        context.put("tags", overriddenParameters.getTags());
        context.put("monochrome", overriddenParameters.isMonochrome());
        context.put("cucumberOutputDir", normalizePathSeparator(config.getCucumberOutputDir()));
        context.put("glue", overriddenParameters.getGlue());
        context.put("className", FilenameUtils.removeExtension(outputFileName));
        context.put("packageName", config.getPackageName());
        context.put("feature",parsedFeature);

        velocityTemplate.merge(context, writer);
    }

    /**
     * Create the format string used for the output.
     */
    private List<String> createPluginStrings() {
        final List<String> formatList = new ArrayList<String>();
        for (final Plugin plugin : overriddenParameters.getPlugins()) {
            formatList.add(plugin.asPluginString(fileCounter));
        }
        return formatList;
    }


    private static final class EscapeJavaReference implements ReferenceInsertionEventHandler {

        public Object referenceInsert(final String reference, final Object value) {
            if (value == null) {
                return null;
            } else {
                return StringEscapeUtils.escapeJava(value.toString());
            }
        }
    }

}
