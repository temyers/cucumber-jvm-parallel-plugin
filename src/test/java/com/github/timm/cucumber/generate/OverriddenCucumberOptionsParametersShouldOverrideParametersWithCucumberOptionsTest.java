package com.github.timm.cucumber.generate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class OverriddenCucumberOptionsParametersShouldOverrideParametersWithCucumberOptionsTest {

    private OverriddenCucumberOptionsParameters params;

    @Before
    public void setup() {
        params = new OverriddenCucumberOptionsParameters();
    }

    @Test
    public void tagsParameterIsOverridden() {
        params.setTags("\"@replaceMe\"");

        params.overrideParametersWithCucumberOptions("--tags @tag1 --tags @tag2");

        assertThat(params.getTags(), equalTo("\"@tag1\",\"@tag2\""));
    }

    @Test
    public void glueParameterIsOverridden() {
        params.setGlue("a.b.c,d.e.f");

        params.overrideParametersWithCucumberOptions("--glue somewhere,somewhere.else");
        assertThat(params.getGlue(), equalTo("somewhere,somewhere.else"));
    }

    @Test
    public void strictParameterIsOverriddenIfSpecified() {
        params.setStrict(false);

        params.overrideParametersWithCucumberOptions("--strict");
        assertThat(params.isStrict(), equalTo(true));

    }

    @Test
    public void strictParameterIsMaintainedIfNotSpecified() {
        params.setStrict(true);

        params.overrideParametersWithCucumberOptions("--glue somewhere,somewhere.else");
        assertThat(params.isStrict(), equalTo(true));
    }

    @Test
    public void strictParameterIsMaintainedIfNotSpecified2() {

        params.setStrict(false);
        params.overrideParametersWithCucumberOptions("--glue somewhere,somewhere.else");
        assertThat(params.isStrict(), equalTo(false));
    }

    @Test
    public void formatParameterIsOverridden() {

        params.setFormat("somethingElse");
        params.overrideParametersWithCucumberOptions(
                        "--format html --plugin pretty --glue somewhere");
        assertThat(params.getFormat(), equalTo("html,pretty"));
    }

    @Test
    public void monochromeParameterIsOverridden() {

        params.setMonochrome(false);
        params.overrideParametersWithCucumberOptions("--monochrome");
        assertThat(params.isMonochrome(), equalTo(true));
    }
}
