import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true,
features = {"/Users/tim/data/projects/personal/cucumber-jvm-parallel-plugin/src/it/junit/issue_43-outline_runner-filter-by-tag/src/test/resources/features/feature2.feature:18"},
plugin = {"json:target/cucumber-parallel/3.json"},
monochrome = false,
tags = {},
glue = { "com.github.timm" })
public class Parallel03IT {
}
