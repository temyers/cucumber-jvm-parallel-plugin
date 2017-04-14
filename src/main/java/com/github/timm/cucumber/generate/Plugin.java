package com.github.timm.cucumber.generate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Plugin {

    private static final Map<String, Plugin> BUILD_IN_PLUGINS = createBuildInPlugins();

    private static Map<String, Plugin> createBuildInPlugins() {
        Map<String, Plugin> plugins = new HashMap<String, Plugin>();
        plugins.put("null", createPluginWithoutOutput("null"));
        plugins.put("junit", new Plugin("junit", "xml"));
        plugins.put("testng", new Plugin("testng", "xml"));
        plugins.put("html", new Plugin("html", null));
        plugins.put("pretty", createPluginWithoutOutput("pretty"));
        plugins.put("progress", new Plugin("progress", "txt"));
        plugins.put("json", new Plugin("json", "json"));
        plugins.put("usage", new Plugin("usage", "json"));
        plugins.put("rerun", new Plugin("rerun", "txt"));
        plugins.put("default_summary", createPluginWithoutOutput("default_summary"));
        plugins.put("null_summary", createPluginWithoutOutput("null_summary"));
        return plugins;
    }

    private String name;

    public boolean isNoOutput() {
        return noOutput;
    }

    public void setNoOutput(boolean noOutput) {
        this.noOutput = noOutput;
    }

    private boolean noOutput;
    private String extension;
    private File outputDirectory;

    public Plugin() {
    }

    private Plugin(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }


    private Plugin(String name, boolean noOutput) {
        this.name = name;
        this.noOutput = noOutput;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    void applyDefaults(File defaultOutputDirectory) {
        if (BUILD_IN_PLUGINS.containsKey(name)) {
            applyDefaultsForBuildinPlugins();
        }

        // disable noOutput when we have an output directory or extension
        if (isNoOutput()) {
            noOutput = outputDirectory == null && extension == null;
        }

        // Unless noOutput is specifically enabled we assume that this plugin creates output.
        if (outputDirectory == null && !isNoOutput()) {
            outputDirectory = defaultOutputDirectory;
        }
    }

    private void applyDefaultsForBuildinPlugins() {
        Plugin knownPlugin = BUILD_IN_PLUGINS.get(name);

        // Unless extension is overridden we assume that this plugin uses the default.
        if (extension == null) {
            extension = knownPlugin.getExtension();
        }

        noOutput = knownPlugin.isNoOutput();
        // Because this plugin is known we always override any user input
        // about what the plugin outputs when it outputs nothing.
        if (noOutput) {
            extension = null;
            outputDirectory = null;
        }
    }

    static Plugin createPluginWithoutOutput(String pluginString) {
        return new Plugin(pluginString, true);
    }

    static Plugin createBuildInPlugin(String name) {
        Plugin plugin = BUILD_IN_PLUGINS.get(name);
        if (plugin == null) {
            throw new IllegalArgumentException(name + " is not a build in cucumber plugin");
        }

        return new Plugin(plugin.name, plugin.extension);
    }

    String asPluginString(int fileCounter) {

        String formatStr = name;

        if (outputDirectory == null) {
            return formatStr;
        }

        formatStr += ":" + normalizePathSeparator(outputDirectory) + "/" + fileCounter;

        if (extension == null) {
            return formatStr;
        }

        return formatStr + "." + extension;
    }

    private static String normalizePathSeparator(File file) {
        return file.getPath().replace(File.separatorChar, '/');
    }

    @Override
    public String toString() {
        return "Plugin{"
            + "name='" + name + '\''
            + ", noOutput=" + noOutput
            + ", extension='" + extension + '\''
            + ", outputDirectory=" + outputDirectory
            + '}';
    }

}
