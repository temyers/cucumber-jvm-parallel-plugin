import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target");
File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/Parallel03IT.java");
File suite04 = new File(basedir, "target/generated-test-sources/cucumber/Parallel04IT.java");
File feature1 = new File(basedir, "/src/test/resources/rerun/rerun1.txt");
File feature2 = new File(basedir, "/src/test/resources/rerun/rerun2.txt");

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
        features = {"src/test/resources/features/feature1.feature:3"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/1.json"},
        monochrome = false,
        glue = {"foo", "bar"})
public class Parallel01IT {
}"""

String expected02 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"src/test/resources/features/feature2.feature:7"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/2.json"},
        monochrome = false,
        glue = {"foo", "bar"})
public class Parallel02IT {
}"""

String expected03 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"src/test/resources/features/feature3.feature:3:12"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/3.json"},
        monochrome = false,
        glue = {"foo", "bar"})
public class Parallel03IT {
}"""


String expected04 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"src/test/resources/features/feature4.feature:12"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/4.json"},
        monochrome = false,
        glue = {"foo", "bar"})
public class Parallel04IT {
}"""


Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
Assert.assertThat(suite03.text, equalToIgnoringWhiteSpace(expected03))
Assert.assertThat(suite04.text, equalToIgnoringWhiteSpace(expected04))