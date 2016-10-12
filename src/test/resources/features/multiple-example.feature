Feature: Multiple Examples

@outlineTag
Scenario Outline: Multiple example blocks
  Given example <number>
  When I generate runners
  Then this scenario may be included
  
  @english
  Examples: English
    | Number |
    | one    |
    | two    |
    | three  |
  
  @german
  Examples: German
    | Number |
    | Ein    |
    | Zwei   |
    | Drei   |