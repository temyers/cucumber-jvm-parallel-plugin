@feature1
Feature: Feature2

  Scenario: Generate Junit Runner for each feature file
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/Parallel01IT.java" should exist
    And it should contain content of expectedFile "src\test\resources\expectedFiles\Parallel01IT.java"