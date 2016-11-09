@feature1
Feature: Feature1

  @required
  Scenario: Matching tags are included
    Then this feature should should be included

  @required
  Scenario: More Matching tags are included
    Then this feature should should be included

  Scenario: Missing tags are excluded
    Then this feature should should be excluded
