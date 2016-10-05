Feature: Feature1

  Scenario: Generate runners per scenario
    Given I have a feature with 2 scenarios
    When I generate runners per scenario
    Then a runner should be created for each scenario

  @outlineTag
  Scenario Outline: Generate runners per example in a scenario outline 
    Given I have a scenario with 3 examples
    When I generate runners per scenario
    Then a runner should be created for <example> example
    
    Examples:
    | example |
    | foo      |
    | bar      |
    | baz      |
    