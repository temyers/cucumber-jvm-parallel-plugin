package com.github.timm.cucumber.generate;

import org.apache.maven.plugin.logging.Log;

import java.io.File;

public interface FileGeneratorConfig {

    boolean filterFeaturesByTags();

    Log getLog();

    File getFeaturesDirectory();

    String getEncoding();

    String getCucumberOutputDir();

    boolean useTestNG();

    String getNamingScheme();

    String getNamingPattern();

    String getCustomVmTemplate();

    String getPackageName();

    File getProjectBasedir();
}
