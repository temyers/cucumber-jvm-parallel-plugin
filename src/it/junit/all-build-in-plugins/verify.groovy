import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target");
File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File feature1 = new File(basedir, "/src/test/resources/features/feature1.feature");

assert suite01.isFile()

String expected01 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature1.absolutePath}"},
        plugin = {"null", "junit:${buildDirectory.absolutePath}/cucumber-parallel/junit/1.xml", "testng:${buildDirectory.absolutePath}/cucumber-parallel/testng/1.xml", "html:${buildDirectory.absolutePath}/cucumber-parallel/html/1", "pretty", "progress:${buildDirectory.absolutePath}/cucumber-parallel/progress/1.txt", "json:${buildDirectory.absolutePath}/cucumber-parallel/json/1.json", "usage:${buildDirectory.absolutePath}/cucumber-parallel/usage/1.json", "rerun:${buildDirectory.absolutePath}/cucumber-parallel/rerun/1.txt", "default_summary", "null_summary"},
        monochrome = false,
        tags = {},
        glue = {"foo"})
public class Parallel01IT {
}"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
  