import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java")
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java")

File feature1 = new File(basedir, "/src/test/resources/features/feature1.feature")
File feature2 = new File(basedir, "/src/test/resources/features/feature2.feature")

assert suite01.isFile()
assert suite02.isFile()

String expected01 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, features = {"${feature1.absolutePath}"}, plugin = {"json:target/my-custom-dir/1.json"},
monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Parallel01IT {
}"""

String expected02 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, features = {"${feature2.absolutePath}"}, plugin = {"json:target/my-custom-dir/2.json"},
monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Parallel02IT {
}"""

// The order of the files isn't important but listFiles may list files in any order
// This ensures we assert correctly despite file ordering
if (suite01.text.contains("feature1")) {
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
} else {
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected02))
}

