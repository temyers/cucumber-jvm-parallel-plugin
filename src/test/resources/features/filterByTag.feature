@featureTag
Feature: filter by tag

@tag1 @tag2
Scenario: with multiple tags
  When I generate runners
  Then this scenario may be included

@tag1
Scenario: with one tag
  Given a scenario with multiple tags
  When I generate runners
  Then this scenario may be included

@tag2
Scenario: with other tag
  Given a scenario with multiple tags
  When I generate runners
  Then this scenario may be included
