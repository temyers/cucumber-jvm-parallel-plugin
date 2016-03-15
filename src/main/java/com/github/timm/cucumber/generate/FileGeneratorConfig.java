package com.github.timm.cucumber.generate;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

public interface FileGeneratorConfig {

    boolean filterFeaturesByTags();

    Log getLog();

    File getFeaturesDirectory();

    String getEncoding();

    String getCucumberOutputDir();

    boolean useTestNG();

}
