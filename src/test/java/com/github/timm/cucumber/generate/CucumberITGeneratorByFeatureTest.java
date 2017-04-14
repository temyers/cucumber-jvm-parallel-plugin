package com.github.timm.cucumber.generate;

import static com.github.timm.cucumber.generate.Plugin.createBuildInPlugin;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

public class CucumberITGeneratorByFeatureTest {

    @SuppressWarnings("deprecation")
    private CucumberITGeneratorByFeature classUnderTest;
    private File outputDirectory;

    @Before
    public void setup() throws Exception {
        TestFileGeneratorConfig config = new TestFileGeneratorConfig()
            .setFeaturesDirectory(
                new File("src/test/resources/features/"))
            .setCucumberOutputDir(this.getClass());

        final OverriddenCucumberOptionsParameters overriddenParameters =
                new OverriddenCucumberOptionsParameters()
                        .setTags(Collections.<String>emptyList())
                        .setGlue(singletonList("foo"))
                        .setStrict(true)
                        .setPlugins(singletonList(createBuildInPlugin("json")))
                        .setMonochrome(false);

        final ClassNamingScheme classNamingScheme =
                        new ClassNamingSchemeFactory(new InstanceCounter()).create("simple", null);

        classUnderTest = new CucumberITGeneratorByFeature(config, overriddenParameters,
                        classNamingScheme);

        outputDirectory = config.getCucumberOutputDir();
        outputDirectory.mkdirs();
        FileUtils.cleanDirectory(outputDirectory);
    }

    @Test
    public void shouldOnlyCreateOneClassPerFile() throws Exception {

        final String featureFile =
                        "src/test/resources/features/filterByTag.feature";
        final int expectedGeneratedFiles = 1;

        outputDirectory.deleteOnExit();
        classUnderTest.generateCucumberITFiles(outputDirectory, singletonList(new File(featureFile)));

        assertThat(outputDirectory.listFiles()).hasSize(expectedGeneratedFiles);


    }


}
