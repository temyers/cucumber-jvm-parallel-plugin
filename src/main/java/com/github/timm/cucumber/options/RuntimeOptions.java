package com.github.timm.cucumber.options;

import cucumber.api.SnippetType;
import cucumber.runtime.CucumberException;
import cucumber.runtime.model.PathWithLines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Copyright (c) 2008-2014 The Cucumber Organisation
 * <p/>
 * Fork of https://github.com/cucumber/cucumber-jvm/blob/master/core/src/main/java/cucumber/runtime/RuntimeOptions.java
 */
public class RuntimeOptions {
    static String usageText;

    private final List<String> glue = new ArrayList<String>();
    private final List<String> filters = new ArrayList<String>();
    private final List<String> pluginNames = new LinkedList<String>();
    private boolean dryRun;
    private boolean strict = false;
    private boolean monochrome = false;
    private final List<String> featurePaths = new ArrayList<String>();
    private SnippetType snippetType;

    /**
     * Create a new instance from a string of options, for example:
     * <p/>
     * <pre>"--name 'the fox' --plugin pretty --strict"</pre>
     *
     * @param argv the arguments
     */
    public RuntimeOptions(final String argv) {
        this(Shellwords.parse(argv));
    }

    /**
     * Create a new instance from a list of options, for example:
     * <p/>
     * <pre>Arrays.asList("--name", "the fox", "--plugin", "pretty", "--strict");</pre>
     *
     * @param argv the arguments
     */
    public RuntimeOptions(List<String> argv) {

        argv = new ArrayList<String>(argv); // in case the one passed in is unmodifiable.
        parse(argv);

    }

    private void parse(final List<String> args) {
        final List parsedFilters = new ArrayList<String>();
        final List parsedFeaturePaths = new ArrayList<String>();
        final List parsedGlue = new ArrayList<String>();

        while (!args.isEmpty()) {
            final String arg = args.remove(0).trim();

            if (arg.equals("--glue") || arg.equals("-g")) {
                final String gluePath = args.remove(0);
                parsedGlue.add(gluePath);
            } else if (arg.equals("--tags") || arg.equals("-t")) {
                parsedFilters.add(args.remove(0));
            } else if (arg.equals("--plugin") || arg.equals("-p")) {
                addPluginName(args.remove(0));
            } else if (arg.equals("--format") || arg.equals("-f")) {
                System.err.println(
                    "WARNING: Cucumber-JVM's --format option is deprecated. "
                        + "Please use --plugin instead.");
                addPluginName(args.remove(0));
            } else if (arg.equals("--no-dry-run") || arg.equals("--dry-run") || arg.equals("-d")) {
                dryRun = !arg.startsWith("--no-");
            } else if (arg.equals("--no-strict") || arg.equals("--strict") || arg.equals("-s")) {
                strict = !arg.startsWith("--no-");
            } else if (arg.equals("--no-monochrome") || arg.equals("--monochrome") || arg
                .equals("-m")) {
                monochrome = !arg.startsWith("--no-");
            } else if (arg.equals("--snippets")) {
                this.snippetType = SnippetType.fromString(args.remove(0));
            } else if (!arg.equals("--name") && !arg.equals("-n")) {
                if (arg.startsWith("-")) {
                   // ignore
                }
                parsedFeaturePaths.add(arg);
            } else {
                Pattern patternFilter = Pattern.compile(args.remove(0));
                parsedFilters.add(patternFilter);
            }
        }

        if (!parsedFilters.isEmpty() || this.haveLineFilters(parsedFeaturePaths)) {
            this.filters.clear();
            this.filters.addAll(parsedFilters);
            System.out.println(parsedFilters.toString());
            if (parsedFeaturePaths.isEmpty() && !this.featurePaths.isEmpty()) {
                this.stripLinesFromFeaturePaths(this.featurePaths);
            }
        }

        if (!parsedFilters.isEmpty()) {
            filters.clear();
            filters.addAll(parsedFilters);
        }
        if (!parsedGlue.isEmpty()) {
            glue.clear();
            glue.addAll(parsedGlue);
        }
    }

    private void addPluginName(final String plugin) {
        pluginNames.add(plugin);
    }

    public List<String> getGlue() {
        return glue;
    }

    public boolean isStrict() {
        return strict;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public List<String> getFilters() {
        return filters;
    }

    public boolean isMonochrome() {
        return monochrome;
    }

    public List<String> getPluginNames() {
        return pluginNames;
    }

    public List<String> getFeaturePaths() {
        return featurePaths;
    }

    private void stripLinesFromFeaturePaths(List<String> featurePaths) {
        ArrayList newPaths = new ArrayList();
        Iterator var3 = featurePaths.iterator();

        while (var3.hasNext()) {
            String pathName = (String) var3.next();
            newPaths.add(PathWithLines.stripLineFilters(pathName));
        }

        featurePaths.clear();
        featurePaths.addAll(newPaths);
    }

    private boolean haveLineFilters(List<String> parsedFeaturePaths) {
        Iterator var2 = parsedFeaturePaths.iterator();

        String pathName;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            pathName = (String) var2.next();
        } while (!pathName.startsWith("@") && !PathWithLines.hasLineFilters(pathName));

        return true;
    }
}
