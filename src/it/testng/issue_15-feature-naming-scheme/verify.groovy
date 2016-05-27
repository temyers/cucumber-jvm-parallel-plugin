import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Feature101IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Feature202IT.java");

assert suite01.isFile()
assert suite02.isFile()

String expected01 = """import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(strict = true, features = {"classpath:features/feature1.feature"}, plugin = {"json:target/cucumber-parallel/1.json",
"pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Feature101IT extends AbstractTestNGCucumberTests {
}"""

String expected02 = """import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(strict = true, features = {"classpath:features/feature2.feature"}, plugin = {"json:target/cucumber-parallel/2.json",
"pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Feature202IT extends AbstractTestNGCucumberTests {
}"""

// Depending on the OS, listFiles can list files in different order.  The actual order of files isn't necessary

if (suite01.text.contains("feature1")) {
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
} else {
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected02))
}

