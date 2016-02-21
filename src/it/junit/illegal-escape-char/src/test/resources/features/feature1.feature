Feature: Feature1

  Scenario: Generate Junit Runner for each feature file
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/1IT.java" should exist
    And it should contain:
    """
    @RunWith(Cucumber.class)
    @CucumberOptions(strict = true, features = {"classpath:features/feature1.feature"}, format = {"json:target/cucumber-reports/1.json",
    "pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
    public class Parallel01IT {
    }
    """