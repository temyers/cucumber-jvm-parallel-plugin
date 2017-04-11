package com.github.timm.cucumber.generate;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.github.timm.cucumber.options.RuntimeOptions;

import java.util.List;

class OverriddenCucumberOptionsParameters {

    private List<String> tags;
    private List<String> glue;
    private boolean strict;
    private List<String> plugins;
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

    public OverriddenCucumberOptionsParameters setPlugins(final List<String> plugins) {
        this.plugins = requireNoneBlank(plugins, "The parameters 'plugins' are missing or invalid");
        return this;
    }

    public OverriddenCucumberOptionsParameters setMonochrome(final boolean monochrome) {
        this.monochrome = monochrome;
        return this;
    }

    void overrideParametersWithCucumberOptions(final String cucumberOptions) {
        if (cucumberOptions == null || cucumberOptions.length() == 0) {
            return;
        }
        final RuntimeOptions options = new RuntimeOptions(cucumberOptions);
        final List<String> tags = options.getFilters();
        if (!tags.isEmpty()) {
            setTags(options.getFilters());
        }

        final List<String> glue = options.getGlue();
        if (!glue.isEmpty()) {
            setGlue(glue);
        }

        if (options.isStrict()) {
            this.strict = true;
        }

        if (!options.getPluginNames().isEmpty()) {
            setPlugins(options.getPluginNames());
        }

        if (options.isMonochrome()) {
            this.monochrome = true;
        }
    }

    public boolean isStrict() {
        return strict;
    }


    public List<String> getPlugins() {
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
