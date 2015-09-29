package com.github.timm.cucumber.generate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
public class GenerateRunnersMojoShouldOverrideParametersWithCucumberOptionsTest {

    private GenerateRunnersMojo mojo;

    @Before
    public void setup() {
        mojo = new GenerateRunnersMojo();
    }

    @Test
    public void tagsParameterIsOverridden() {
        mojo.setCucumberOptions("--tags @tag1 --tags @tag2");
        mojo.setTags("\"@replaceMe\"");

        mojo.overrideParametersWithCucumberOptions();

        assertThat(mojo.getTags(),equalTo("\"@tag1\",\"@tag2\""));
    }

    @Test
    public void glueParameterIsOverridden() {
        mojo.setCucumberOptions("--glue somewhere,somewhere.else");
        mojo.setGlue("a.b.c,d.e.f");

        mojo.overrideParametersWithCucumberOptions();
        assertThat(mojo.getGlue(),equalTo("somewhere,somewhere.else"));
    }

    @Test
    public void strictParameterIsOverriddenIfSpecified() {
        mojo.setCucumberOptions("--strict");
        mojo.setStrict(false);

        mojo.overrideParametersWithCucumberOptions();
        assertThat(mojo.isStrict(),equalTo(true));

    }

    @Test
    public void strictParameterIsMaintainedIfNotSpecified() {
        mojo.setCucumberOptions("--glue somewhere,somewhere.else");
        mojo.setStrict(true);

        mojo.overrideParametersWithCucumberOptions();
        assertThat(mojo.isStrict(),equalTo(true));
    }

    @Test
    public void strictParameterIsMaintainedIfNotSpecified2() {
        mojo.setCucumberOptions("--glue somewhere,somewhere.else");

        mojo.setStrict(false);
        mojo.overrideParametersWithCucumberOptions();
        assertThat(mojo.isStrict(),equalTo(false));
    }

    @Test
    public void formatParameterIsOverridden() {
        mojo.setCucumberOptions("--format html --plugin pretty --glue somewhere");

        mojo.setFormat("somethingElse");
        mojo.overrideParametersWithCucumberOptions();
        assertThat(mojo.getFormat(),equalTo("html,pretty"));
    }
    @Test
    public void monochromeParameterIsOverridden() {
        mojo.setCucumberOptions("--monochrome");

        mojo.setMonochrome(false);
        mojo.overrideParametersWithCucumberOptions();
        assertThat(mojo.isMonochrome(),equalTo(true));
    }
}
