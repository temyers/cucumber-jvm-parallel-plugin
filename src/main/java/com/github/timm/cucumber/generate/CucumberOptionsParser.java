package com.github.timm.cucumber.generate;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CucumberOptionsParser {
    public static final String[] TAG_DELIMITERS = new String[] { "-t", "--tags" };

    private final List<String> args;

    public CucumberOptionsParser(String cucumberOptions) {
        this.args = new ArrayList<String>(asList(extractArgs(cucumberOptions)));
    }

    private String[] extractArgs(String cucumberOptions) {
        return cucumberOptions.replaceAll(",\\s+", ",").split("\\s+");
    }

    public String parse(String[] delimiters) {
        List<String> parsedArgs = new ArrayList<String>();
        while (!args.isEmpty()) {
            String arg = args.remove(0).trim();
            if (StringUtils.join(delimiters).contains(arg)) {
                parsedArgs.add("\"" + args.remove(0) + "\"");
            }
        }
        return parsedArgs.isEmpty() ? null : StringUtils.join(parsedArgs, ",");
    }
}
