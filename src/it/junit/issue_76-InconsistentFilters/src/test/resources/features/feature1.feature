Feature: Feature1

  @complete
  Scenario: Generate Junit Runner for each feature file
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/1IT.java" should exist
    And it should contain:
    """
    @RunWith(Cucumber.class)
    @CucumberOptions(strict = true, features = {"classpath:features/feature1.feature:4"}, format = {"json:target/cucumber-parallel/1.json",
    "pretty"}, monochrome = false, glue = { "foo", "bar" })
    public class Parallel01IT {
    }
    """
