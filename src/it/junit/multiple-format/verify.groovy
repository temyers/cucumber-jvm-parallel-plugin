import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target");

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");

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
        plugin = {"html:${buildDirectory.absolutePath}/cucumber-parallel/1", "json:${buildDirectory.absolutePath}/cucumber-parallel/1.json", "pretty"},
        monochrome = false,
        tags = {"@complete", "@accepted"},
        glue = {"foo"})
public class Parallel01IT {
}"""

String expected02 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature2.absolutePath}"},
        plugin = {"html:${buildDirectory.absolutePath}/cucumber-parallel/2", "json:${buildDirectory.absolutePath}/cucumber-parallel/2.json", "pretty"},
        monochrome = false,
        tags = {"@complete", "@accepted"},
        glue = {"foo"})
public class Parallel02IT {
}"""

// Depending on the OS, listFiles can list files in different order.  The actual order of files isn't necessary

if (suite01.text.contains("feature1")) {
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
} else {
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected02))
}

