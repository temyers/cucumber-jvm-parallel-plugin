@feature-tag2
Feature: Feature2

  @outline-tag
  Scenario Outline:  Generate File with tags from the scenario, example and feature level
    Given I have feature files
    When I generate Maven sources
    Then the file "target/generated-test-sources/"<file>" should exist
    And it should contain:
    """
    // This is a custom template for Parallel01IT with an array of tags <tags>
    """

  @example1
    Examples:
      | file            | tags                                           |
      | Parallel02IT    | {"@feature-tag2", "@outline-tag", "@example1"} |

  @example2
    Examples:
      | file            | tags                                           |
      | Parallel03IT    | {"@feature-tag2", "@outline-tag", "@example2"} |