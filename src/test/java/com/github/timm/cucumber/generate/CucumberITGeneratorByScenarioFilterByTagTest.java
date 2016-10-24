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

public class CucumberITGeneratorByScenarioFilterByTagTest {

    CucumberITGeneratorByScenario classUnderTest;
    private TestFileGeneratorConfig config;
    private File outputDirectory;
    private OverriddenCucumberOptionsParameters overriddenParameters;

    @Before
    public void setup() throws Exception {
        config = new TestFileGeneratorConfig()
                        .setFilterFeaturesByTags(true)
                        .setCucumberOutputDir(this.getClass());

        overriddenParameters = new OverriddenCucumberOptionsParameters();
        overriddenParameters.setGlue("foo").setStrict(true).setFormat("json").setTags("")
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
    // TODO - refactor SRP
    public void shouldIncludeScenariosWhenFilteringByTagsAtFeatureLevel() throws Exception {

        //given
        overriddenParameters.setTags("\"@feature1\"");
        config.setFeaturesDirectory(
                        new File("src/it/junit/filter-by-tag3-and/src/test/resources/features/"));

        final String featureFile1 =
                        "src/it/junit/filter-by-tag3-and/src/test/resources/features/feature1.feature";
        final String featureFile2 =
                        "src/it/junit/filter-by-tag3-and/src/test/resources/features/feature2.feature";
        final String featureFile3 =
                        "src/it/junit/filter-by-tag3-and/src/test/resources/features/feature3.feature";

        //when
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile1),new File(featureFile2),new File(featureFile3)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/feature1.feature:5"));

        assertThat(outputDirectory.listFiles()).hasSize(1);

    }

    @Test
    // TODO - refactor SRP
    public void shouldIncludeScenariosWhenFilteringByTagsAtScenarioLevel() throws Exception {

        overriddenParameters.setTags("\"@required\"");
        config.setFeaturesDirectory(
                        new File("src/it/junit/filter-by-tag3-and/src/test/resources/features/"));

        final String featureFile1 =
                        "src/it/junit/filter-by-tag3-and/src/test/resources/features/feature1.feature";
        final String featureFile2 =
                        "src/it/junit/filter-by-tag3-and/src/test/resources/features/feature2.feature";
        final String featureFile3 =
                        "src/it/junit/filter-by-tag3-and/src/test/resources/features/feature3.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile1),new File(featureFile2),new File(featureFile3)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/feature1.feature:5"));
        final File scenario2 = new File(outputDirectory, "Parallel02IT.java");
        assertThat(scenario2).satisfies(new FileContains("features/feature2.feature:5"));

        assertThat(outputDirectory.listFiles()).hasSize(2);

    }

    @Test
    // TODO - refactor SRP
    public void shouldIncludeScenariosWhenFilteringByTagsAtScenarioOutlineLevel() throws Exception {

        overriddenParameters.setTags("\"@outlineTag\"");
        config.setFeaturesDirectory(
                        new File("src/it/junit/issue_43-outline_runner/src/test/resources/features/"));

        final String featureFile1 =
                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/feature1.feature";
        final String featureFile2 =
                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/feature2.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile1),new File(featureFile2)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/feature2.feature:16"));
        final File scenario2 = new File(outputDirectory, "Parallel02IT.java");
        assertThat(scenario2).satisfies(new FileContains("features/feature2.feature:17"));

        assertThat(outputDirectory.listFiles()).hasSize(3);

    }

    @Test
    // TODO - refactor SRP
    public void shouldIncludeScenariosWhenFilteringByOrTags() throws Exception {

        overriddenParameters.setTags("\"@tag1,@tag2\"");
        config.setFeaturesDirectory(
                        new File("src/test/resources/features/"));

        final String featureFile1 =
                        "src/test/resources/features/filterByTag.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile1)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/filterByTag.feature:5"));
        final File scenario2 = new File(outputDirectory, "Parallel02IT.java");
        assertThat(scenario2).satisfies(new FileContains("features/filterByTag.feature:10"));
        final File scenario3 = new File(outputDirectory, "Parallel03IT.java");
        assertThat(scenario3).satisfies(new FileContains("features/filterByTag.feature:16"));

        assertThat(outputDirectory.listFiles()).hasSize(3);

    }

    @Test
    // TODO - refactor SRP
    public void shouldIncludeScenariosWhenFilteringByAndTags() throws Exception {

        overriddenParameters.setTags("\"@tag1\",\"@tag2\"");
        config.setFeaturesDirectory(
                        new File("src/test/resources/features/"));

        final String featureFile1 =
                        "src/test/resources/features/filterByTag.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile1)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/filterByTag.feature:5"));

        assertThat(outputDirectory.listFiles()).hasSize(1);

    }

    @Test
    // TODO - refactor SRP
    public void shouldIncludeScenariosWhenFilteringByNotTags() throws Exception {

        overriddenParameters.setTags("\"@tag1\",\"~@tag2\"");
        config.setFeaturesDirectory(
                        new File("src/test/resources/features/"));

        final String featureFile1 =
                        "src/test/resources/features/filterByTag.feature";
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile1)));

        final File scenario1 = new File(outputDirectory, "Parallel01IT.java");
        assertThat(scenario1).satisfies(new FileContains("features/filterByTag.feature:10"));

        assertThat(outputDirectory.listFiles()).hasSize(1);

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
