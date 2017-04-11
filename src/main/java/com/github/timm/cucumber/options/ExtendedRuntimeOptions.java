package com.github.timm.cucumber.options;

import cucumber.api.SnippetType;
import cucumber.api.StepDefinitionReporter;
import cucumber.api.SummaryPrinter;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Env;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.Utils;
import cucumber.runtime.formatter.ColorAware;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.formatter.StrictAware;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.PathWithLines;
import gherkin.I18n;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.util.FixJava;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by sugad.mankar on 11/23/2015.
 */
public class ExtendedRuntimeOptions extends RuntimeOptions {
    public static final String VERSION = ResourceBundle.getBundle("cucumber.version").getString(
        "cucumber-jvm.version");
    public static final String USAGE_RESOURCE = "/cucumber/api/cli/USAGE.txt";
    static String usageText;
    private final List<String> glue;
    private final List<Object> filters;
    private final List<String> featurePaths;
    private final List<String> pluginFormatterNames;
    private final List<String> pluginStepDefinitionReporterNames;
    private final List<String> pluginSummaryPrinterNames;
    private final PluginFactory pluginFactory;
    private final List<Object> plugins;
    private boolean dryRun;
    private boolean strict;
    private boolean monochrome;
    private SnippetType snippetType;
    private boolean pluginNamesInstantiated;

    public ExtendedRuntimeOptions(String argv) {
        this(new PluginFactory(), Shellwords.parse(argv));
    }

    public ExtendedRuntimeOptions(List<String> argv) {
        this(new PluginFactory(), argv);
    }

    public ExtendedRuntimeOptions(Env env, List<String> argv) {
        this(env, new PluginFactory(), argv);
    }

    public ExtendedRuntimeOptions(PluginFactory pluginFactory, List<String> argv) {
        this(Env.INSTANCE, pluginFactory, argv);
    }

    public ExtendedRuntimeOptions(Env env, PluginFactory pluginFactory, List<String> argv) {
        super(env, pluginFactory, argv);
        this.glue = new ArrayList();
        this.filters = new ArrayList();
        this.featurePaths = new ArrayList();
        this.pluginFormatterNames = new ArrayList();
        this.pluginStepDefinitionReporterNames = new ArrayList();
        this.pluginSummaryPrinterNames = new ArrayList();
        this.plugins = new ArrayList();
        this.strict = false;
        this.monochrome = false;
        this.snippetType = SnippetType.UNDERSCORE;
        this.pluginFactory = pluginFactory;
        ArrayList argv1 = new ArrayList(argv);
        this.parse(argv1);
        /*
         * String cucumberOptionsFromEnv = env.get("cucumber.options");
         * System.out.println("Cucumber options from env"+ cucumberOptionsFromEnv);
         * if(cucumberOptionsFromEnv != null){
         * this.parse(Shellwords.parse(cucumberOptionsFromEnv)); }
         */
        if (this.pluginFormatterNames.isEmpty()) {
            this.pluginFormatterNames.add("progress");
        }

        if (this.pluginSummaryPrinterNames.isEmpty()) {
            this.pluginSummaryPrinterNames.add("default_summary");
        }

    }

    static void loadUsageTextIfNeeded() {
        if (usageText == null) {
            try {
                InputStreamReader e = new InputStreamReader(
                    FixJava.class.getResourceAsStream("/cucumber/api/cli/USAGE.txt"), "UTF-8");
                usageText = FixJava.readReader(e);
            } catch (Exception var1) {
                usageText = "Could not load usage text: " + var1.toString();
            }
        }

    }

    private void parse(List<String> args) {
        ArrayList parsedFilters = new ArrayList();
        ArrayList parsedFeaturePaths = new ArrayList();
        ArrayList parsedGlue = new ArrayList();

        while (true) {
            while (true) {
                while (true) {
                    while (!args.isEmpty()) {
                        String arg = ((String) args.remove(0)).trim();
                        if (!arg.equals("--help") && !arg.equals("-h")) {
                            if (!arg.equals("--version") && !arg.equals("-v")) {
                                String nextArg;
                                if (arg.equals("--i18n")) {
                                    nextArg = (String) args.remove(0);
                                    System.exit(this.printI18n(nextArg));
                                } else if (!arg.equals("--glue") && !arg.equals("-g")) {
                                    if (!arg.equals("--tags") && !arg.equals("-t")) {
                                        if (!arg.equals("--plugin") && !arg.equals("-p")) {
                                            if (!arg.equals("--format")
                                                && !arg.equals("-f")) {
                                                if (!arg.equals("--no-dry-run")
                                                    && !arg.equals("--dry-run")
                                                    && !arg.equals("-d")) {
                                                    if (!arg.equals("--no-strict")
                                                        && !arg.equals("--strict")
                                                        && !arg.equals("-s")) {
                                                        if (!arg.equals("--no-monochrome")
                                                            && !arg.equals("--monochrome")
                                                            && !arg.equals("-m")) {
                                                            if (arg.equals("--snippets")) {
                                                                nextArg = (String) args.remove(0);
                                                                this.snippetType =
                                                                    SnippetType.fromString(nextArg);
                                                            } else if (!arg.equals("--name")
                                                                && !arg.equals("-n")) {
                                                                if (arg.startsWith("-")) {
                                                                    this.printUsage();
                                                                    throw new CucumberException(
                                                                        "Unknown option: " + arg);
                                                                }

                                                                parsedFeaturePaths.add(arg);
                                                            } else {
                                                                nextArg = (String) args.remove(0);
                                                                System.out.println("Next arg"
                                                                    + nextArg);
                                                                Pattern patternFilter =
                                                                    Pattern.compile(nextArg);
                                                                parsedFilters.add(patternFilter);
                                                            }
                                                        } else {
                                                            this.monochrome =
                                                                !arg.startsWith("--no-");
                                                        }
                                                    } else {
                                                        this.strict = !arg.startsWith("--no-");
                                                    }
                                                } else {
                                                    this.dryRun = !arg.startsWith("--no-");
                                                }
                                            } else {
                                                System.err
                                                    .println("WARNING: Cucumber-JVM\'s"
                                                        + " --format option is deprecated."
                                                        + " Please use --plugin instead.");
                                                this.addPluginName((String) args.remove(0));
                                            }
                                        } else {
                                            this.addPluginName((String) args.remove(0));
                                        }
                                    } else {
                                        parsedFilters.add(args.remove(0));
                                    }
                                } else {
                                    nextArg = (String) args.remove(0);
                                    parsedGlue.add(nextArg);
                                }
                            } else {
                                System.out.println(VERSION);
                                System.exit(0);
                            }
                        } else {
                            this.printUsage();
                            System.exit(0);
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

                    if (!parsedFeaturePaths.isEmpty()) {
                        this.featurePaths.clear();
                        this.featurePaths.addAll(parsedFeaturePaths);
                    }

                    if (!parsedGlue.isEmpty()) {
                        this.glue.clear();
                        this.glue.addAll(parsedGlue);
                    }

                    return;
                }
            }
        }
    }

    private void addPluginName(String name) {
        if (PluginFactory.isFormatterName(name)) {
            this.pluginFormatterNames.add(name);
        } else if (PluginFactory.isStepDefinitionResporterName(name)) {
            this.pluginStepDefinitionReporterNames.add(name);
        } else {
            if (!PluginFactory.isSummaryPrinterName(name)) {
                throw new CucumberException("Unrecognized plugin: " + name);
            }

            this.pluginSummaryPrinterNames.add(name);
        }

    }

    private boolean haveLineFilters(List<String> parsedFeaturePaths) {
        Iterator var2 = parsedFeaturePaths.iterator();

        String pathName;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            pathName = (String) var2.next();
        }
        while (!pathName.startsWith("@") && !PathWithLines.hasLineFilters(pathName));

        return true;
    }

    private void stripLinesFromFeaturePaths(List<String> featurePaths) {
        ArrayList newPaths = new ArrayList();
        Iterator var3 = featurePaths.iterator();

        while (var3.hasNext()) {
            String pathName = (String) var3.next();
            System.out.println("Path Name" + pathName);
            newPaths.add(PathWithLines.stripLineFilters(pathName));
        }

        featurePaths.clear();
        featurePaths.addAll(newPaths);
    }

    private void printUsage() {
        loadUsageTextIfNeeded();
        System.out.println(usageText);
    }

    private int printI18n(String language) {
        List all = I18n.getAll();
        if (!language.equalsIgnoreCase("help")) {
            return this.printKeywordsFor(language, all);
        } else {
            Iterator var3 = all.iterator();

            while (var3.hasNext()) {
                I18n i18n = (I18n) var3.next();
                System.out.println(i18n.getIsoCode());
            }

            return 0;
        }
    }

    private int printKeywordsFor(String language, List<I18n> all) {
        Iterator var3 = all.iterator();

        I18n i18n;
        do {
            if (!var3.hasNext()) {
                System.err.println("Unrecognised ISO language code");
                return 1;
            }

            i18n = (I18n) var3.next();
        }
        while (!i18n.getIsoCode().equalsIgnoreCase(language));

        System.out.println(i18n.getKeywordTable());
        return 0;
    }

    public List<CucumberFeature> cucumberFeatures(ResourceLoader resourceLoader) {
        return CucumberFeature.load(resourceLoader, this.featurePaths, this.filters, System.out);
    }

    List<Object> getPlugins() {
        if (!this.pluginNamesInstantiated) {
            Iterator var1 = this.pluginFormatterNames.iterator();

            String pluginName;
            Object plugin;
            while (var1.hasNext()) {
                pluginName = (String) var1.next();
                plugin = this.pluginFactory.create(pluginName);
                this.plugins.add(plugin);
                this.setMonochromeOnColorAwarePlugins(plugin);
                this.setStrictOnStrictAwarePlugins(plugin);
            }

            var1 = this.pluginStepDefinitionReporterNames.iterator();

            while (var1.hasNext()) {
                pluginName = (String) var1.next();
                plugin = this.pluginFactory.create(pluginName);
                this.plugins.add(plugin);
            }

            var1 = this.pluginSummaryPrinterNames.iterator();

            while (var1.hasNext()) {
                pluginName = (String) var1.next();
                plugin = this.pluginFactory.create(pluginName);
                this.plugins.add(plugin);
            }

            this.pluginNamesInstantiated = true;
        }

        return this.plugins;
    }

    public Formatter formatter(ClassLoader classLoader) {
        return (Formatter) this.pluginProxy(classLoader, Formatter.class);
    }

    public Reporter reporter(ClassLoader classLoader) {
        return (Reporter) this.pluginProxy(classLoader, Reporter.class);
    }

    public StepDefinitionReporter stepDefinitionReporter(ClassLoader classLoader) {
        return (StepDefinitionReporter) this.pluginProxy(classLoader, StepDefinitionReporter.class);
    }

    public SummaryPrinter summaryPrinter(ClassLoader classLoader) {
        return (SummaryPrinter) this.pluginProxy(classLoader, SummaryPrinter.class);
    }

    public <T> T pluginProxy(ClassLoader classLoader, final Class<T> type) {
        Object proxy = Proxy.newProxyInstance(classLoader, new Class[] {type},
            new InvocationHandler() {
                public Object invoke(Object target, Method method, Object[] args) throws Throwable {
                    Iterator var4 = ExtendedRuntimeOptions.this.getPlugins().iterator();

                    while (true) {
                        Object plugin;
                        do {
                            if (!var4.hasNext()) {
                                return null;
                            }

                            plugin = var4.next();
                        }
                        while (!type.isInstance(plugin));

                        try {
                            Utils.invoke(plugin, method, 0L, args);
                        } catch (Throwable var7) {
                            if (!method.getName().equals("startOfScenarioLifeCycle")
                                && !method.getName().equals("endOfScenarioLifeCycle")) {
                                throw var7;
                            }
                        }
                    }
                }
            });
        return type.cast(proxy);
    }

    private void setMonochromeOnColorAwarePlugins(Object plugin) {
        if (plugin instanceof ColorAware) {
            ColorAware colorAware = (ColorAware) plugin;
            colorAware.setMonochrome(this.monochrome);
        }

    }

    private void setStrictOnStrictAwarePlugins(Object plugin) {
        if (plugin instanceof StrictAware) {
            StrictAware strictAware = (StrictAware) plugin;
            strictAware.setStrict(this.strict);
        }

    }

    public List<String> getGlue() {
        return this.glue;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public boolean isDryRun() {
        return this.dryRun;
    }

    public List<String> getFeaturePaths() {
        return this.featurePaths;
    }

    public void addPlugin(Object plugin) {
        this.plugins.add(plugin);
    }

    public List<Object> getFilters() {
        return this.filters;
    }

    public boolean isMonochrome() {
        return this.monochrome;
    }

    public SnippetType getSnippetType() {
        return this.snippetType;
    }
}
