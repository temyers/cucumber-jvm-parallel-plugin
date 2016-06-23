import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true,
    features = {"classpath:features/feature1.feature"},
    plugin = {"json:target/my-custom-dir/1.json", "pretty"},
    monochrome = false,
    tags = {"@complete"},
    glue = { "foo", "bar" })
public class Parallel01IT {
}
