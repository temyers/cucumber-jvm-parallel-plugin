package com.github.timm.cucumber.generate;

import static org.fest.assertions.Assertions.assertThat;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class CucumberITGeneratorByFeatureTest {

    CucumberITGeneratorByFeature classUnderTest;
    private TestFileGeneratorConfig config;
    private File outputDirectory;

    @Before
    public void setup() throws Exception {
        config = new TestFileGeneratorConfig()
                        .setFeaturesDirectory(
                                        new File("src/test/resources/features/"))
                        .setCucumberOutputDir(this.getClass());

        final OverriddenCucumberOptionsParameters overriddenParameters =
                        new OverriddenCucumberOptionsParameters();
        overriddenParameters.setTags("").setGlue("foo").setStrict(true).setFormat("json")
        .setMonochrome(false);

        final ClassNamingScheme classNamingScheme =
                        new ClassNamingSchemeFactory(new InstanceCounter()).create("simple", null);

        classUnderTest = new CucumberITGeneratorByFeature(config, overriddenParameters,
                        classNamingScheme);

        outputDirectory = new File(config.getCucumberOutputDir());
        outputDirectory.mkdirs();
        FileUtils.cleanDirectory(outputDirectory);
    }

    @Test
    public void shouldOnlyCreateOneClassPerFile() throws Exception {

        final String featureFile =
                        "src/test/resources/features/filterByTag.feature";
        final int expectedGeneratedFiles = 1;

        outputDirectory.deleteOnExit();
        classUnderTest.generateCucumberITFiles(outputDirectory,
                        Arrays.asList(new File(featureFile)));

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);


    }


}
