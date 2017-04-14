package com.github.timm.cucumber.generate;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OverriddenCucumberOptionsParameters {

    private List<String> tags;
    private List<String> glue;
    private boolean strict;
    private List<Plugin> plugins;
    private boolean monochrome;

    public OverriddenCucumberOptionsParameters setTags(final List<String> tags) {
        this.tags = requireNoneBlank(tags, "The parameters 'tags' are missing or invalid");
        return this;
    }

    public OverriddenCucumberOptionsParameters setGlue(final List<String> glue) {
        if (glue == null || glue.isEmpty()) {
            throw new IllegalArgumentException("The parameters 'glue' are missing or invalid");
        }
        this.glue = requireNoneBlank(glue, "The parameters 'glue' are invalid");

        return this;
    }

    public OverriddenCucumberOptionsParameters setStrict(final boolean strict) {
        this.strict = strict;
        return this;
    }

    public OverriddenCucumberOptionsParameters setPlugins(final List<Plugin> plugins) {
        this.plugins = requireNoDuplicateExtensions(plugins);
        return this;
    }

    private static List<Plugin> requireNoDuplicateExtensions(List<Plugin> plugins) {
        final Map<String, Plugin> extension = new HashMap<String, Plugin>();
        for (Plugin plugin : plugins) {
            if (plugin.isNoOutput()) {
                continue;
            }

            final Plugin replaced =
                extension.put(plugin.getOutputDirectory() + ":" + plugin.getExtension(), plugin);
            if (replaced != null) {
                throw new IllegalArgumentException("The cucumber plugin '" + plugin
                    + "' is writing to the same "
                    + "combination of outputDirectory and extension as '"
                    + replaced + "'. Use the outputDirectories attribute to "
                    + "specify a directory for each plugin");
            }
        }

        return plugins;
    }

    public OverriddenCucumberOptionsParameters setMonochrome(final boolean monochrome) {
        this.monochrome = monochrome;
        return this;
    }

    OverriddenCucumberOptionsParameters overridePlugins(List<Plugin> plugins) {
        if (!plugins.isEmpty()) {
            setPlugins(plugins);
        }
        return this;
    }

    OverriddenCucumberOptionsParameters overrideGlue(List<String> glue) {
        if (!glue.isEmpty()) {
            setGlue(glue);
        }
        return this;
    }

    OverriddenCucumberOptionsParameters overrideTags(List<String> tags) {
        if (!tags.isEmpty()) {
            setTags(tags);
        }
        return this;
    }

    OverriddenCucumberOptionsParameters overrideStrict(Boolean strict) {
        if (strict != null) {
            this.strict = strict;
        }
        return this;
    }


    OverriddenCucumberOptionsParameters overrideMonochrome(Boolean monochrome) {
        if (monochrome != null) {
            this.monochrome = monochrome;
        }
        return this;
    }

    public boolean isStrict() {
        return strict;
    }


    public List<Plugin> getPlugins() {
        return plugins;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isMonochrome() {
        return monochrome;
    }

    public List<String> getGlue() {
        return glue;
    }

    private static List<String> requireNoneBlank(List<String> list, String message) {
        for (String element : list) {
            if (isBlank(element)) {
                throw new IllegalArgumentException(message + ": " + list);
            }
        }
        return list;
    }
}
