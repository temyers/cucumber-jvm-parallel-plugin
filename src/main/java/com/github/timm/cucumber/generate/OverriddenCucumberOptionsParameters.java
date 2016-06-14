package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.options.RuntimeOptions;
import com.github.timm.cucumber.options.TagParser;
import org.apache.commons.lang.StringUtils;

import java.util.List;

class OverriddenCucumberOptionsParameters {

    private String tags;
    private String glue;
    private boolean strict;
    private String format;
    private boolean monochrome;

    public OverriddenCucumberOptionsParameters setTags(final String tags) {
        this.tags = tags;
        return this;
    }

    public OverriddenCucumberOptionsParameters setGlue(final String glue) {
        this.glue = glue;
        return this;
    }

    public OverriddenCucumberOptionsParameters setStrict(final boolean strict) {
        this.strict = strict;
        return this;
    }

    public OverriddenCucumberOptionsParameters setFormat(final String format) {
        this.format = format;
        return this;
    }

    public OverriddenCucumberOptionsParameters setMonochrome(final boolean monochrome) {
        this.monochrome = monochrome;
        return this;
    }

    public void overrideParametersWithCucumberOptions(final String cucumberOptions) {
        if (cucumberOptions == null || cucumberOptions.isEmpty()) {
            return;
        }
        final RuntimeOptions options = new RuntimeOptions(cucumberOptions);
        final List<String> tags = options.getFilters();
        final String parsedTags = TagParser.parseTags(tags);
        if (!parsedTags.isEmpty()) {
            this.tags = parsedTags;
        }

        final List<String> glue = options.getGlue();
        if (!glue.isEmpty()) {
            this.glue = StringUtils.join(glue, ",");
        }

        if (options.isStrict()) {
            this.strict = true;
        }

        if (!options.getPluginNames().isEmpty()) {
            this.format = StringUtils.join(options.getPluginNames(), ",");
        }

        if (options.isMonochrome()) {
            this.monochrome = true;
        }
    }

    public boolean isStrict() {
        return strict;
    }


    public String getFormat() {
        return format;
    }

    public String getTags() {
        return tags;
    }

    public boolean isMonochrome() {
        return monochrome;
    }

    public String getGlue() {
        return glue;
    }

}
