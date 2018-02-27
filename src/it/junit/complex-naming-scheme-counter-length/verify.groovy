import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target");

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01FooFeature1Counter001IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel00FooFeature2Counter002IT.java");
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01FooFeature3Counter003IT.java");
File suite04 = new File(basedir, "target/generated-test-sources/cucumber/Parallel00FooFeature4Counter004IT.java");

File feature1 = new File(basedir, "/src/test/resources/features/feature1.feature");
File feature2 = new File(basedir, "/src/test/resources/features/feature2.feature");
File feature3 = new File(basedir, "/src/test/resources/features/feature3.feature");
File feature4 = new File(basedir, "/src/test/resources/features/feature4.feature");

assert suite01.isFile()
assert suite02.isFile()
assert suite03.isFile()
assert suite04.isFile()

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
public class Parallel01FooFeature1Counter001IT {
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
public class Parallel00FooFeature2Counter002IT {
}"""

String expected03 =
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
public class Parallel01FooFeature3Counter003IT {
}"""

String expected04 =
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
public class Parallel00FooFeature4Counter004IT {
}"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
Assert.assertThat(suite03.text, equalToIgnoringWhiteSpace(expected03))
Assert.assertThat(suite04.text, equalToIgnoringWhiteSpace(expected04))
