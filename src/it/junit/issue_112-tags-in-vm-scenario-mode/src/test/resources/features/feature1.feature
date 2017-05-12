@featureTag
@myOtherFeatureTag("Value")
Feature: Feature1

  @scenarioTag1
  @myOtherScenarioTag("Value")
  Scenario: Ensure Velocity feature variable available for scenario
    Given I have feature files with tags on feature and scenarios
    And I have a custom VM file using the supplied feature 
    When I generate runners per scenario
    Then the file "target/generated-test-sources/Parallel01IT.java" should exist
    And it should contain:
    """
    // This is a custom template for Parallel01IT
    """

  @outlineTag
  @myOtherScenarioOutlineTag("Value")
  Scenario Outline: Ensure Velocity feature variable available for scenario outline 
    Given I have a scenario with tags on feature and scenarios with 2 examples
    When I generate runners per scenario
    Then a runner should be created for <example> example
    And the file "target/generated-test-sources/Parallel02IT.java" should exist
    And the file "target/generated-test-sources/Parallel03IT.java" should exist
    
    
    Examples:
    | example |
    | foo      |
    | bar      |
       