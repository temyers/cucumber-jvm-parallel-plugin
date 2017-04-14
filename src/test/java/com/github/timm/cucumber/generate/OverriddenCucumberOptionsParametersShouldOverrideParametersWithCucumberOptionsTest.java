package com.github.timm.cucumber.generate;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.github.timm.cucumber.options.RuntimeOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class OverriddenCucumberOptionsParametersShouldOverrideParametersWithCucumberOptionsTest {

    private OverriddenCucumberOptionsParameters params;

    @Before
    public void setup() {
        params = new OverriddenCucumberOptionsParameters();
    }

    @Test
    public void tagsParameterIsOverridden() {
        params.setTags(singletonList("\"@replaceMe\""));

        params.overrideTags(new RuntimeOptions("--tags @tag1 --tags @tag2").getFilters());
        assertThat(params.getTags(), equalTo(asList("@tag1","@tag2")));
    }

    @Test
    public void glueParameterIsOverridden() {
        params.setGlue(asList("a.b.c","d.e.f"));

        params.overrideGlue(new RuntimeOptions("--glue somewhere --glue somewhere.else").getGlue());
        assertThat(params.getGlue(), equalTo(asList("somewhere","somewhere.else")));
    }

    @Test
    public void strictParameterIsOverriddenIfSpecified() {
        params.setStrict(false);

        params.overrideStrict(new RuntimeOptions("--strict").isStrict());
        assertThat(params.isStrict(), equalTo(true));
    }

    @Test
    public void strictParameterIsOverriddenIfSpecified2() {
        params.setStrict(true);

        params.overrideStrict(new RuntimeOptions("--no-strict").isStrict());
        assertThat(params.isStrict(), equalTo(false));
    }

    @Test
    public void strictParameterIsMaintainedIfNotSpecified() {
        params.setStrict(true);

        params.overrideStrict(new RuntimeOptions("--glue somewhere,somewhere.else").isStrict());
        assertThat(params.isStrict(), equalTo(true));
    }

    @Test
    public void strictParameterIsMaintainedIfNotSpecified2() {

        params.setStrict(false);
        params.overrideStrict(new RuntimeOptions("--glue somewhere,somewhere.else").isStrict());
        assertThat(params.isStrict(), equalTo(false));
    }

    @Test
    public void formatParameterIsOverridden() {

        params.setPlugins(singletonList(Plugin.createBuildInPlugin("html")));
        params.overridePlugins(singletonList(Plugin.createBuildInPlugin("json")));
        assertEquals("json", params.getPlugins().get(0).getName());
    }

    @Test
    public void formatParameterIsMaintainedIfNotSpecified() {
        params.setPlugins(singletonList(Plugin.createBuildInPlugin("html")));
        params.overridePlugins(Collections.<Plugin>emptyList());
        assertEquals("html", params.getPlugins().get(0).getName());
    }

    @Test
    public void monochromeParameterIsOverridden() {

        params.setMonochrome(false);
        params.overrideMonochrome(new RuntimeOptions("--monochrome").isMonochrome());
        assertThat(params.isMonochrome(), equalTo(true));
    }

    @Test
    public void monochromeParameterIsOverridden2() {
        params.setMonochrome(true);
        params.overrideMonochrome(new RuntimeOptions("--no-monochrome").isMonochrome());
        assertThat(params.isMonochrome(), equalTo(false));
    }


    @Test
    public void monochromeParameterIsMaintainedIfNotSpecified() {
        params.setMonochrome(false);
        params.overrideMonochrome(new RuntimeOptions("--glue somewhere,somewhere.else").isMonochrome());
        assertThat(params.isMonochrome(), equalTo(false));
    }

    @Test
    public void monochromeParameterIsMaintainedIfNotSpecified2() {
        params.setMonochrome(true);
        params.overrideMonochrome(new RuntimeOptions("--glue somewhere,somewhere.else").isMonochrome());
        assertThat(params.isMonochrome(), equalTo(true));
    }
}
