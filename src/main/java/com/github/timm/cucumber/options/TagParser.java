package com.github.timm.cucumber.options;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class TagParser {

    public static String parseTags(final List<String> tags) {

        if(tags.isEmpty()) {
            return "";
        }

        return '"' + StringUtils.join(tags, "\",\"") + '"';

    }

}
