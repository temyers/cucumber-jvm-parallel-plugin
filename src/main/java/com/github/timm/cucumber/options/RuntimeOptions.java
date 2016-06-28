package com.github.timm.cucumber.options;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Copyright (c) 2008-2014 The Cucumber Organisation.
 *
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

    /**
     * Create a new instance from a string of options, for example:
     *
     * <pre>
     * "--name 'the fox' --plugin pretty --strict"
     * </pre>
     *
     * @param argv the arguments
     */
    public RuntimeOptions(final String argv) {
        this(Shellwords.parse(argv));
    }

    /**
     * Create a new instance from a list of options, for example:
     *
     * <pre>
     * Arrays.asList("--name", "the fox", "--plugin", "pretty", "--strict");
     * </pre>
     *
     * @param argv the arguments
     */
    public RuntimeOptions(List<String> argv) {

        argv = new ArrayList<String>(argv); // in case the one passed in is unmodifiable.
        parse(argv);

    }

    private void parse(final List<String> args) {
        final List<String> parsedFilters = new ArrayList<String>();
        // final List<String> parsedFeaturePaths = new ArrayList<String>();
        final List<String> parsedGlue = new ArrayList<String>();

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
                System.err.println("WARNING: Cucumber-JVM's --format option is deprecated. "
                    + "Please use --plugin instead.");
                addPluginName(args.remove(0));
            } else if (arg.equals("--no-dry-run") || arg.equals("--dry-run") || arg.equals("-d")) {
                dryRun = !arg.startsWith("--no-");
            } else if (arg.equals("--no-strict") || arg.equals("--strict") || arg.equals("-s")) {
                strict = !arg.startsWith("--no-");
            } else if (arg.equals("--no-monochrome") || arg.equals("--monochrome")
                || arg.equals("-m")) {
                monochrome = !arg.startsWith("--no-");
            } else {
                // ignore
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

}
