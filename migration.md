Migration Notes
===============

These notes cover things to be aware of when upgrading the cucumber-jvm-parallel-plugin.

Migration from version 4.x
==========================
* Gherkin has been updated to version 5.0.0. You may encounter issues if you do not also upgrade
  Cucumber to at least version 2.x.x.
* Tag expressions have upgraded to 1.1.1. See https://docs.cucumber.io/tag-expressions

Migration from version 3.x
==========================
* The `filterFeaturesByTags` property has been removed. If you have not set this property to true 
  remove any tags from your configuration. If you have set it to true you  can safely remove it.


Migration from version 2.x
==========================
* The `glue` property now takes a list of `package` elements rather then a list of comma delimited packages. A 
  `package` contains a single package name to be used as glue.
* The `tags` property now takes a list of `tag` elements rather then a list of comma and quote delimited strings. A 
  `tag` element can take one or more tags. The semantics are the same as those of the
  [the command line]( https://github.com/cucumber/cucumber/wiki/Tags#running-a-subset-of-scenarios).
* The default value for `cucumberOutputDir` has changed from `target/cucumber-parallel` to 
  `${project.build.directory}/cucumber-parallel` if you were using a build directory other then `target` you can remove 
  this element.


Migration from version 1.x
==========================
* The default for the `tags` property is now set to no tags.  This means that all scenarios shall be executed by default.  
      If you did not specify the tags parameter, add `<tags>"@complete", "@accepted"</tags>` to the configuration section.
* A new property `parallelScheme` has been introduced to provide a mechanism for choosing how the feature runners are created.  The default is set to SCENARIO, meaning that a runner will be created for each scenario in a feature file, including any examples.  This greatly increases the possibility for running scenarios in parallel, but has the impact that any fragile, interdependent, tests may fail unexpectedly.  Use the FEATURE property to generate runners as per 1.x