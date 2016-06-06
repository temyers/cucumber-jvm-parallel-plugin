import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");

assert suite01.isFile()
assert suite02.isFile()

String expected01 =
        """import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(strict = true, features = {"classpath:features/feature1.feature"}, plugin = {"json:target/my-custom-dir/1.json",
	"pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Parallel01IT extends AbstractTestNGCucumberTests {
}"""

String expected02 =
        """import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(strict = true, features = {"classpath:features/feature2.feature"}, plugin = {"json:target/my-custom-dir/2.json",
"pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Parallel02IT extends AbstractTestNGCucumberTests {
}"""

// Depending on the OS, listFiles can list files in different order.  The actual order of files isn't necessary

if (suite01.text.contains("feature1")) {
    System.out.println(suite01.text)
    System.out.println(suite02.text)
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
} else {
    Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected01))
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected02))
}

