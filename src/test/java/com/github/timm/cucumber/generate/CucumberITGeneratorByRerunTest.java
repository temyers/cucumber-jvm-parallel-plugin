package com.github.timm.cucumber.generate;

import static com.github.timm.cucumber.generate.Plugin.createBuildInPlugin;
import static java.util.Collections.singletonList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.fest.assertions.Assertions.assertThat;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.maven.plugin.MojoExecutionException;
import org.fest.assertions.Condition;
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CucumberITGeneratorByRerunTest {

    CucumberITGeneratorByRerun classUnderTest;
    private TestFileGeneratorConfig config;
    private File outputDirectory;

    @Before
    public void setup() throws Exception {
        config = new TestFileGeneratorConfig()
                        .setCucumberOutputDir(this.getClass());

        final OverriddenCucumberOptionsParameters overriddenParameters =
                        new OverriddenCucumberOptionsParameters()
                                        .setTags(Collections.<String>emptyList())
                                        .setGlue(singletonList("foo")).setStrict(true)
                                        .setPlugins(singletonList(createBuildInPlugin("json")))
                                        .setMonochrome(false);

        final ClassNamingScheme classNamingScheme =
                        new ClassNamingSchemeFactory(new InstanceCounter()).create("simple", null);

        classUnderTest = new CucumberITGeneratorByRerun(config, overriddenParameters,
                        classNamingScheme);

        outputDirectory = config.getCucumberOutputDir();
        outputDirectory.mkdirs();
        FileUtils.cleanDirectory(outputDirectory);
    }

    @Test
    public void shouldCreateCorrectNumberOfFilesGivenRerunFolder() throws Exception {

        final String rerunDirectoryPath = "src/it/junit/simple-rerun/src/test/resources/rerun";
        File featuresDirectory = new File(rerunDirectoryPath);

        final int expectedGeneratedFiles = 4;

        outputDirectory.deleteOnExit();
        if (!featuresDirectory.exists()) {
            throw new MojoExecutionException("Rerun directory does not exist");
        }

        final Collection<File> featureFiles =
                        listFiles(featuresDirectory, new String[] {"txt"}, true);
        final List<File> sortedFeatureFiles =
                        new NameFileComparator().sort(new ArrayList<File>(featureFiles));

        classUnderTest.generateCucumberITFiles(outputDirectory, sortedFeatureFiles);

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);
    }

    @Test
    public void shouldCreateCorrectNumberOfFilesGivenRerunFile() throws Exception {

        final String rerunFile =
                        "src/it/junit/simple-rerun/src/test/resources/rerun/rerun1.txt";
        final int expectedGeneratedFiles = 3;

        outputDirectory.deleteOnExit();
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        singletonList(new File(rerunFile)));

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);
    }

    @Test
    public void shouldCreateCorrectNumberOfFilesGivenSingleRowFile() throws Exception {

        final String rerunFile =
                        "src/it/junit/simple-rerun/src/test/resources/rerun/rerun2.txt";
        final int expectedGeneratedFiles = 1;

        classUnderTest.generateCucumberITFiles(outputDirectory,
                        singletonList(new File(rerunFile)));

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);

    }

    @Test
    public void shouldIncludeTheScenarioLineNumberInGeneratedRunner() throws Exception {
        final String rerunFile =
                        "src/it/junit/simple-rerun/src/test/resources/rerun/rerun2.txt";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        singletonList(new File(rerunFile)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/feature4.feature:12"));
    }

    private static final class FileContains extends Condition<File> {

        private final String expectedContent;

        FileContains(final String expectedContent) {
            this.expectedContent = expectedContent;
        }

        @Override
        public boolean matches(final File file) {

            try {
                return FileUtils.readFileToString(file).contains(expectedContent);
            } catch (final IOException e) {
                Fail.fail("could not check file contents", e);
                return false;
            }
        }

    }

}
