import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

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
        plugin = {"path.to.my.formaters.NoOutputFormatter"},
        monochrome = false,
        tags = {},
        glue = {"path.to.my.formaters"})
public class Parallel01IT {
}"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
  