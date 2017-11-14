#language:de
Funktionalität: Feature1

  Szenario: Generate Junit Runner for each feature file
    Gegeben sei I have feature files
    Wenn I generate Maven sources
    Dann the file "target/generated-test-sources/1IT.java" should exist
    Und it should contain:
    """
    @RunWith(Cucumber.class)
    @CucumberOptions(
        strict = true,
        features = {"classpath:features/feature1.feature"},
        format = {"json:target/cucumber-parallel/1.json",
    "pretty"},
        monochrome = false,
        tags = {"@complete", "@accepted"},
        glue = {"foo", "bar"})
    public class Parallel01IT {
    }
    """