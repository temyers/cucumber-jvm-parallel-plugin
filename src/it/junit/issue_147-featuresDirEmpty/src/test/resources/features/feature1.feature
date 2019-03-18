Scenario: Features directory is empty
  Given the features directory is empty
  When I perform a build
  Then a warning log message should be created
  But the build should succeed
  And no runners should be generated