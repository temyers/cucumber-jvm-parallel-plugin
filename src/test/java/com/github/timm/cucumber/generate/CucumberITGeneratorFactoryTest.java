package com.github.timm.cucumber.generate;

import static org.fest.assertions.Assertions.assertThat;

import com.github.timm.cucumber.generate.name.ClassNamingScheme;
import com.github.timm.cucumber.generate.name.ClassNamingSchemeFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class CucumberITGeneratorFactoryTest {

    private CucumberITGeneratorFactory factory = new CucumberITGeneratorFactory(null, null, null);

    @Before
    public void setup() throws Exception {
        final FileGeneratorConfig config = new TestFileGeneratorConfig()
                        .setCucumberOutputDir(this.getClass()).setFeaturesDirectory(new File(
                                        "src/it/junit/issue_43-outline_runner/src/test/resources/features/"));

        final OverriddenCucumberOptionsParameters overriddenParameters =
                        new OverriddenCucumberOptionsParameters();
        overriddenParameters.setTags("").setGlue("foo").setStrict(true).setFormat("json")
        .setMonochrome(false);

        final ClassNamingScheme classNamingScheme =
                        new ClassNamingSchemeFactory(new InstanceCounter()).create("simple", null);

        factory = new CucumberITGeneratorFactory(config, overriddenParameters, classNamingScheme);
    }

    @Test
    public void shouldCreateFeatureRunner() throws Exception {
        final CucumberITGenerator generator = factory.create(ParallelScheme.FEATURE);
        assertThat(generator).isInstanceOf(CucumberITGeneratorByFeature.class);
    }

    @Test
    public void shouldCreateScenarioRunner() throws Exception {
        final CucumberITGenerator generator = factory.create(ParallelScheme.SCENARIO);
        assertThat(generator).isInstanceOf(CucumberITGeneratorByScenario.class);
    }

}
