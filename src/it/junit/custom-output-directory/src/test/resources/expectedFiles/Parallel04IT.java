import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
    strict = true,
    features = {"classpath:features/feature2.feature"},
    plugin = {"json:target/my-custom-dir/4.json", "pretty"},
    monochrome = false,
    tags = {"@accepted"},
    glue = { "foo", "bar" })
public class Parallel04IT {
}