package com.github.timm.cucumber.generate;

import static org.fest.assertions.Assertions.assertThat;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import org.apache.commons.io.FileUtils;
import org.fest.assertions.Condition;
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CucumberITGeneratorByScenarioTest {

    CucumberITGeneratorByScenario classUnderTest;
    private TestFileGeneratorConfig config;
    private File outputDirectory;

    @Before
    public void setup() throws Exception {
        config = new TestFileGeneratorConfig()
                        .setFeaturesDirectory(
                                        new File("src/it/junit/issue_43-outline_runner/src/test/resources/features/"))
                        .setCucumberOutputDir(this.getClass());

        final OverriddenCucumberOptionsParameters overriddenParameters =
                        new OverriddenCucumberOptionsParameters();
        overriddenParameters.setTags("").setGlue("foo").setStrict(true).setFormat("json")
        .setMonochrome(false);

        final ClassNamingScheme classNamingScheme =
                        new ClassNamingSchemeFactory(new InstanceCounter()).create("simple", null);

        classUnderTest = new CucumberITGeneratorByScenario(config, overriddenParameters,
                        classNamingScheme);

        outputDirectory = new File(config.getCucumberOutputDir());
        outputDirectory.mkdirs();
        FileUtils.cleanDirectory(outputDirectory);
    }

    @Test
    public void shouldCreateCorrectNumberOfFilesGivenFeatures() throws Exception {

        final String featureFile =
                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/feature1.feature";
        final int expectedGeneratedFiles = 1;

        outputDirectory.deleteOnExit();
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile)));

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);


    }

    @Test
    public void shouldCreateCorrectNumberOfFilesGivenFeatures2() throws Exception {

        final String featureFile =
                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/feature2.feature";
        final int expectedGeneratedFiles = 4;

        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile)));

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);

    }

    @Test
    public void shouldIncludeAllExamplesWhenMultipleExampleBlocksAreUsed() throws Exception {
        config.setFeaturesDirectory(new File("src/test/resources/features/"));
        final String featureFile =
                        "src/test/resources/features/multiple-example.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile)));

        final File example4 = new File(outputDirectory, "Parallel04IT.java");
        assertThat(example4).satisfies(new FileContains("features/multiple-example.feature:19"));
        assertThat(outputDirectory.listFiles()).hasSize(6);
    }

    @Test
    public void shouldIncludeTheScenarioLineNumberInGeneratedRunner() throws Exception {
        final String featureFile =
                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/feature2.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/feature2.feature:3"));
    }

    @Test
    public void shouldIncludeTheScenarioOutlineExampleLineNumberInGeneratedRunner()
                    throws Exception {
        final String featureFile =
                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/feature2.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile)));

        final File scenario3 = new File(outputDirectory, "Parallel03IT.java");
        assertThat(scenario3).satisfies(new FileContains("features/feature2.feature:17"));
    }

    private class FileContains extends Condition<File> {

        private final String expectedContent;

        public FileContains(final String expectedContent) {
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
