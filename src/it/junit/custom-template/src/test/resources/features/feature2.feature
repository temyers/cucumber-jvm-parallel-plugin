Feature: Feature2

  Scenario: Generate Custom File for each feature file
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/Parallel02IT.java" should exist
    And it should contain:
    """
    // This is a custom template for Parallel02IT
    """