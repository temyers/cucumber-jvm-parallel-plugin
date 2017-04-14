package com.github.timm.cucumber.generate;

import org.apache.maven.plugin.logging.Log;

import java.io.File;

public interface FileGeneratorConfig {

    boolean filterFeaturesByTags();

    Log getLog();

    String getEncoding();

    File getCucumberOutputDir();

    boolean useTestNG();

    String getCustomVmTemplate();

    String getPackageName();

    File getProjectBasedir();
}
