package com.github.timm.cucumber.generate;

import org.apache.maven.plugin.logging.Log;

import java.io.File;

class TestFileGeneratorConfig implements FileGeneratorConfig {

    private Log log;
    private File featuresDirectory;
    private File outputDir;
    private final boolean useTestNg = false;
    private final String namingScheme = "simple";
    private final String namingPattern = null;
    private final String customVmTemplate = "";

    TestFileGeneratorConfig setFeaturesDirectory(final File directory) {
        this.featuresDirectory = directory;
        return this;
    }

    TestFileGeneratorConfig setCucumberOutputDir(final Class<?> classUnderTest) {
        this.outputDir = new File("target", classUnderTest.getSimpleName());
        return this;
    }

    public Log getLog() {
        return log;
    }

    public String getEncoding() {
        return "UTF-8";
    }

    public File getCucumberOutputDir() {
        return outputDir;
    }

    public boolean useTestNG() {
        return useTestNg;
    }

    public String getCustomVmTemplate() {
        return customVmTemplate;
    }

    public String getPackageName() {
        return null;
    }

    public File getProjectBasedir() {
        return new File(".");
    }

}
