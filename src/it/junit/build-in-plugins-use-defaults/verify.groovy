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
        plugin = {"pretty", "html:${buildDirectory.absolutePath}/cucumber-parallel/1", "json:${buildDirectory.absolutePath}/cucumber-parallel/1.json"},
        monochrome = false,
        tags = {},
        glue = {"foo"})
public class Parallel01IT {
}"""


Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
  