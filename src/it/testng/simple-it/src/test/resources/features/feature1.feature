Feature: Feature1

  Scenario: Generate TestNG Runner for each feature file
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/1IT.java" should exist
    And it should contain:
    """
    @CucumberOptions(strict = true, features = {"classpath:features/feature1.feature"}, format = {"json:target/cucumber-parallel/1.json",
    "pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
    public class Parallel01IT extends AbstractTestNGCucumberTests {
    }
    """