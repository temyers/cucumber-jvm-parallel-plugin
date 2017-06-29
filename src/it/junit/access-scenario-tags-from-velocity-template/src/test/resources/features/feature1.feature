@featuretag
Feature: Feature1

  @scenariotag1
  Scenario: Generate File with tags from the scenario and feature level
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/Parallel01IT.java" should exist
    And it should contain:
    """
    // This is a custom template for Parallel01IT with an array of tags {"@featuretag", "@scenarioag1"}
    """

