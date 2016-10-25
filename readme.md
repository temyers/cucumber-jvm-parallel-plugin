cucumber-jvm-parallel-plugin ![CI status](https://travis-ci.org/temyers/cucumber-jvm-parallel-plugin.svg?branch=master)
============================

A common approach for running Cucumber features in parallel is to create a suite of Cucumber runners, one for each suite of tests you wish to run in parallel.  For maximum parallelism, there should be a runner per feature file.

This is a pain to maintain and not very DRY.

This is where the cucumber-jvm-parallel-plugin comes in.  This plugin automatically generates a Cucumber JUnit or TestNG runner for each scenario/feature file found in your project.

Usage
-----

Add the following to your POM file:

```xml
<plugin>
  <groupId>com.github.temyers</groupId>
  <artifactId>cucumber-jvm-parallel-plugin</artifactId>
  <version>1.2.1</version>
  <executions>
    <execution>
      <id>generateRunners</id>
      <phase>generate-test-sources</phase>
      <goals>
        <goal>generateRunners</goal>
      </goals>
      <configuration>
          <!-- Mandatory -->
          <!-- comma separated list of package names to scan for glue code -->
         <glue>foo, bar</glue>
         <!-- These are optional, with the default values -->
          <!-- Where to output the generated tests -->
           <outputDirectory>${project.build.directory}/generated-test-sources/cucumber</outputDirectory>
           <!-- The diectory, which must be in the root of the runtime classpath, containing your feature files.  -->
          <featuresDirectory>src/test/resources/features/</featuresDirectory>
           <!-- Directory where the cucumber report files shall be written  -->
          <cucumberOutputDir>target/cucumber-parallel</cucumberOutputDir>
          <!-- comma separated list of output formats -->
         <format>json</format>
         <!-- CucumberOptions.strict property -->
         <strict>true</strict>
         <!-- CucumberOptions.monochrome property -->
         <monochrome>true</monochrome>
         <!-- The tags to run, maps to CucumberOptions.tags property -->
         <tags></tags>
         <!-- If set to true, only feature files containing the required tags shall be generated. -->
         <filterFeaturesByTags>false</filterFeaturesByTags>
         <!-- Generate TestNG runners instead of JUnit ones. --> 
         <useTestNG>false</useTestNG>
         <!-- The naming scheme to use for the generated test classes.  One of 'simple' or 'feature-title' --> 
         <namingScheme>simple</namingScheme>
         <!-- The class naming pattern to use.  Only required/used if naming scheme is 'pattern'.-->
         <namingPattern>Parallel{c}IT</namingPattern>
         <!-- One of [SCENARIO, FEATURE]. SCENARIO generates one runner per scenario.  FEATURE generates a runner per feature. -->
         <parallelScheme>SCENARIO<parallelScheme>
      </configuration>
    </execution>
  </executions>
</plugin>
```

If `cucumber.options` VM argument is specified as per the [Cucumber CLI options](https://cucumber.io/docs/reference/jvm), they shall override the configuration tags.

Where glue is a comma separated list of package names to use for the Cucumber Glue.

The plugin will search `featuresDirectory` for `*.feature` files and generate a JUnit test for each one. 
> **WARNING:** `featuresDirectory` must denote a directory within the root of the classpath.
> **Example:** 
> * Resources in `src/test/resources` are added to the classpath by default.
> * `src/test/resources/features` **is** in the root of the classpath, so **would be valid** for `featuresDirectory`
> * `src/test/resources/features/sub_folder` is **not** in the root of the classpath, so **would not be valid** to put in `featuresDirectory`

If you prefer to generate TestNG tests, set `useTestNG` to true

The Java source is generated in `outputDirectory`, based on the naming scheme used.

Each runner is configured to output the results to a separate output file under `target/cucumber-parallel`

###Naming Scheme

The naming scheme used for the generated files is controlled by the `namingScheme` property.  The following values are supported:

| Property      | Generated Name |
| ------------- | -------------- |
| simple        | `ParallelXXIT.java`, where `XX` is a one up counter.
| feature-title | The name is generated based on the feature title with the following rules to ensure it is a valid classname:
* Spaces are removed, camel-casing the title.
* If the feature file starts with a digit, the classname is prefixed with '_'
* A on up counter is appended to the classname, to prevent clashes. |
| pattern       | Generate the filename based on the `namingPattern` property.  
The following tokens can be used in the pattern:
* `{f}` Converts the feature file name to a valid class name using the rules for feature-title, apart from the one up counter.
* `{c}` Adds a one up counter. |

By default, generated test files use the `simple` naming strategy.

####Note on Pattern Naming Scheme
The `pattern` naming scheme is for advanced usage only.  

It is up to you to ensure that class names generated are valid and there are no clashes.  If the same class name is generated multiple times, then it shall be overwritten and some of your tests will not be executed.

The `namingPattern` property is for the **class name** only.  Do not add the `.java` suffix.


FAQ
===
Q. Why isn't there much activity on this project

A. The plugin is considered feature complete.  If you feel there is something missing, raise an issue.

Migration from version 1.x
==========================
* The default for the `tags` property is now set to no tags.  This means that all scenarios shall be executed by default.  
      If you did not specify the tags parameter, add `<tags>"@complete", "@accepted"</tags>` to the configuration section.
* A new property `parallelScheme` has been introduced to provide a mechanism for choosing how the feature runners are created.  The default is set to SCENARIO, meaning that a runner will be created for each scenario in a feature file, including any examples.  This greatly increases the possibility for running scenarios in parallel, but has the impact that any fragile, interdependent, tests may fail unexpectedly.  Use the FEATURE property to generate runners as per 1.x


Changelog
=========
2.0.0
-----
* issue 50 - Support generating runners without any tags.
* PR #57 - Use "generate-test-sources" maven build phase rather than "validate"
* PR #xx - Support generating runners per scenario

1.3.0
-----
* Fix issue #49 - remove dots when using feature file naming scheme
* PR #46 - Add checkstyle rules
* Update to use CucumberOptions.plugin, rather than format
* issue #19 Add support for custom naming scheme based on a pattern.
* PR #28 Use "plugin" rather than "format" in Cucumber.Options.  Supports cucumber 1.2.2+

1.2.1
-----
* issue #15 (re-opened) Fix compilations errors generated by 1.2.0

1.2.0
-----
* issue #15 Add option to name the generated test case based on the name of the feature file.

1.1.0
-----
* pr #13 - Added support for generating TestNG runners.

1.0.1
-----
* issue#10 - compilation error on Windows.
* issue#7 - Added support for filtering generated files by tag.
* Added support for cucumber.options command line arguments.

Contributing
============

To contribute:

* Create an integration test to demonstrate the behaviour under `src/it/`.  For example, to add support for multiple output formats for junit runners:
    * Create src/it/junit/multiple-format
    * copy the contents of the src/it/junit/simple-it directory and update the pom/src as appropriate to demonstrate the configuration.  Update the verify.groovy to implement the test for your feature.
    * Run `mvn clean install -Prun-its` to run the integration tests.
* Implement the feature
* When all tests are passing, submit a pull request.
* Coding standards, based on the [Google Style Guide](https://github.com/google/styleguide) are enforced using Checkstyle.  Formatters for Eclipse and IntelliJ are provided in the `doc/style` folder

Once the pull request has been merged, a new release will be performed as soon as practicable.

Release Process
---------------
See [Maven Central upload guide](https://maven.apache.org/guides/mini/guide-central-repository-upload.html)
