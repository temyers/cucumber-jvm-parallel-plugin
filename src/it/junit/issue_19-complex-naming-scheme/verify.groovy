import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target");

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature101IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature202IT.java");

File feature1 = new File(basedir, "/src/test/resources/features/feature1.feature");
File feature2 = new File(basedir, "/src/test/resources/features/feature2.feature");

assert suite01.isFile()
assert suite02.isFile()

String expected01 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature1.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/1.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature101IT {
}"""

String expected02 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature2.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/2.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature202IT {
}"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
