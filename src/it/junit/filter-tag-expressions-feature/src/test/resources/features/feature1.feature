@europe
Feature: Feature1

  Scenario: Generate Junit Runner for each feature file
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/1IT.java" should exist
    And it should contain:

    @germany
    Scenario: Generate Junit Runner for each feature file second
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/1IT.java" should exist
    And it should contain:
    