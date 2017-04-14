package com.github.timm.cucumber.generate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.io.File;

public class PluginTest {

    @Test
    public void shouldCreateBuildInPlugin() {
        assertThat(Plugin.createBuildInPlugin("html"), notNullValue());
    }

    @Test
    public void shouldCreatePluginWithoutOutput() {
        assertThat(Plugin.createPluginWithoutOutput("quite").isNoOutput(), is(true));
    }

    @Test
    public void applyDefaultAddsOutputDirectoryWhenAbsent() {
        Plugin plugin = new Plugin();
        plugin.setName("path.to.my.XmlFormatter");
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.getOutputDirectory(), equalTo(new File("path/to/output")));
    }

    @Test
    public void applyDefaultRespectsOutputDirectoryWhenPresent() {
        Plugin plugin = new Plugin();
        plugin.setName("path.to.my.XmlFormatter");
        plugin.setOutputDirectory(new File("path/to/output"));
        plugin.applyDefaults(new File("path/to/other/output"));
        assertThat(plugin.getOutputDirectory(), equalTo(new File("path/to/output")));
    }


    @Test
    public void applyDefaultsCorrectsNoOutputIfExtensionIsPresent() {
        Plugin plugin = new Plugin();
        plugin.setName("path.to.my.XmlFormatter");
        plugin.setExtension("xml");
        plugin.setNoOutput(true);

        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.isNoOutput(), equalTo(false));
    }

    @Test
    public void applyDefaultsCorrectsNoOutputIfExtensionIsNotPresent() {
        Plugin plugin = new Plugin();
        plugin.setName("path.to.my.SilentFormatter");
        plugin.setNoOutput(true);

        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.isNoOutput(), equalTo(true));
    }

    @Test
    public void applyDefaultsCorrectsNoOutputIfOutputDirectoryIsPresent() {
        Plugin plugin = new Plugin();
        plugin.setName("path.to.my.XmlFormatter");
        plugin.setOutputDirectory(new File("path/to/output"));
        plugin.setNoOutput(true);

        plugin.applyDefaults(new File("path/to/other/output"));
        assertThat(plugin.isNoOutput(), equalTo(false));
    }

    @Test
    public void applyDefaultsCorrectsNoOutputIfOutputDirectoryIsNotPresent() {
        Plugin plugin = new Plugin();
        plugin.setName("path.to.my.SilentFormatter");
        plugin.setNoOutput(true);
        plugin.applyDefaults(new File("path/to/other/output"));
        assertThat(plugin.isNoOutput(), equalTo(true));
    }

    @Test
    public void applyDefaultsIgnoresExtensionForBuildInPluginsWithNoOutput() {
        Plugin plugin = Plugin.createBuildInPlugin("pretty");
        plugin.setExtension("txt");
        plugin.applyDefaults(new File("path/to/other/output"));
        assertThat(plugin.isNoOutput(), equalTo(true));
        assertThat(plugin.getExtension(), nullValue());
        assertThat(plugin.getOutputDirectory(), nullValue());
    }

    @Test
    public void applyDefaultsIgnoresOutputDirectoryForBuildInPluginsWithNoOutput() {
        Plugin plugin = Plugin.createBuildInPlugin("pretty");
        plugin.setOutputDirectory(new File("path/to/output"));
        plugin.applyDefaults(new File("path/to/other/output"));
        assertThat(plugin.isNoOutput(), equalTo(true));
        assertThat(plugin.getExtension(), nullValue());
        assertThat(plugin.getOutputDirectory(), nullValue());
    }


    @Test
    public void applyDefaultSetsExtensionForBuildInWhenAbsent() {
        Plugin plugin = new Plugin();
        plugin.setName("json");
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.getExtension(), equalTo("json"));
    }

    @Test
    public void applyDefaultRespectsExtensionForBuildInWhenPresent() {
        Plugin plugin = new Plugin();
        plugin.setName("json");
        plugin.setExtension("somethingelse");
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.getExtension(), equalTo("somethingelse"));
    }

    @Test
    public void applyDefaultAlwaysCorrectsNoOutputForBuildIn() {
        Plugin plugin = new Plugin();
        plugin.setName("json");
        plugin.setNoOutput(true);
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.isNoOutput(), equalTo(false));
    }


    @Test
    public void asPluginStringForNoOutPut() {
        Plugin plugin = new Plugin();
        plugin.setName("pretty");
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.asPluginString(42), equalTo("pretty"));
    }

    @Test
    public void asPluginStringForNoExtension() {
        Plugin plugin = new Plugin();
        plugin.setName("html");
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.asPluginString(42), equalTo("html:path/to/output/42"));
    }

    @Test
    public void asPluginString() {
        Plugin plugin = new Plugin();
        plugin.setName("json");
        plugin.applyDefaults(new File("path/to/output"));
        assertThat(plugin.asPluginString(42), equalTo("json:path/to/output/42.json"));
    }



}
