cucumber-jvm-parallel-plugin ![CI status](https://travis-ci.org/temyers/cucumber-jvm-parallel-plugin.svg?branch=master)
============================

A common approach for running Cucumber features in parallel is to create a suite of Cucumber runners, one for each suite of tests you wish to run in parallel.  For maximum parallelism, there should be a runner per feature file.

This is a pain to maintain and not very DRY.

This is where the cucumber-jvm-parallel-plugin comes in.  This plugin automatically generates a Cucumber JUnit or TestNG runner for each scenario/feature file found in your project.

Note: As of `cucumber-jvm:4.0.0` parallel execution is supported natively by cucumber.

Quickstart
----------

Place your feature files in `src/test/resources/features/`

Add the following to your POM file, updating the `<glue>` definition to point to your glue code packages:
```xml
<plugin>
  <groupId>com.github.temyers</groupId>
  <artifactId>cucumber-jvm-parallel-plugin</artifactId>
  <version>5.0.0</version>
  <executions>
    <execution>
      <id>generateRunners</id>
      <phase>generate-test-sources</phase>
      <goals>
        <goal>generateRunners</goal>
      </goals>
      <configuration>
        <!-- Mandatory -->
        <!-- List of package names to scan for glue code. -->
        <glue>
          <package>com.example</package>
          <package>com.example.other</package>
        </glue>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Configure the [Maven Failsafe](http://maven.apache.org/surefire/maven-failsafe-plugin/) to execute your runners.

Generated JUnit test runners will be named `ParallelNIT.java`, where `N` is a one up number.
Cucumber JSON reports will be output to: `target/cucumber-parallel`

Detailed Configuration
----------------------

The following snippet details all the possible configuration options and example usage:
Please refer to the integration tests for example usage:

```xml
<plugin>
  <groupId>com.github.temyers</groupId>
  <artifactId>cucumber-jvm-parallel-plugin</artifactId>
  <version>5.0.0</version>
  <executions>
    <execution>
      <id>generateRunners</id>
      <phase>generate-test-sources</phase>
      <goals>
        <goal>generateRunners</goal>
      </goals>
      <configuration>
        <!-- Mandatory -->
        <!-- List of package names to scan for glue code. -->
        <glue>
          <package>com.example</package>
          <package>com.example.other</package>
        </glue>
        <!-- These are optional, with the default values -->
        <!-- Where to output the generated tests -->
        <outputDirectory>${project.build.directory}/generated-test-sources/cucumber</outputDirectory>
        <!-- The directory, which must be in the root of the runtime classpath, containing your feature files.  -->
        <featuresDirectory>src/test/resources/features/</featuresDirectory>
        <!-- Directory where the cucumber report files shall be written  -->
        <cucumberOutputDir>target/cucumber-parallel</cucumberOutputDir>
        <!-- List of cucumber plugins. When none are provided the json formatter is used. For more 
             advanced usage see section about configuring cucumber plugins -->
        <plugins>
          <plugin>
              <name>json</name>
          </plugin>
          <plugin>
              <name>com.example.CustomHtmlFormatter</name>
              <extension>html</extension>
          </plugin>
        </plugins>
        <!-- CucumberOptions.strict property -->
        <strict>true</strict>
        <!-- CucumberOptions.monochrome property -->
        <monochrome>true</monochrome>
        <!-- The tags to run, maps to CucumberOptions.tags property. Default is no tags. -->
        <tags>
          <tag>@billing</tag>
          <tag>~@billing</tag>
          <tag>@important</tag>
          <tag>@important,@billing</tag>
        </tags>
        <!-- Generate TestNG runners instead of JUnit ones. --> 
        <useTestNG>false</useTestNG>
        <!-- The naming scheme to use for the generated test classes.  One of ['simple', 'feature-title', 'pattern'] -->
        <namingScheme>simple</namingScheme>
        <!-- The class naming pattern to use.  Only required/used if naming scheme is 'pattern'.-->
        <namingPattern>Parallel{c}IT</namingPattern>
        <!-- One of [SCENARIO, FEATURE]. SCENARIO generates one runner per scenario.  FEATURE generates a runner per feature. -->
        <parallelScheme>SCENARIO</parallelScheme>
        <!-- Specify a custom template for the generated sources (this is a path relative to the project base directory) -->
        <customVmTemplate>src/test/resources/cucumber-custom-runner.vm</customVmTemplate>
        <!-- Specify a custom package name for generated sources. Default is no package.-->
        <packageName>com.example</packageName>
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

### Cucumber Plugins ###

Cucumber plugins that write to a file are referenced using the syntax 
`name[:outputDirectory/excutorId[.extension]]`. Because not all plugins create output and the 
excutorId is provided at runtime some leg work is needed.

#### Build-in Cucumber Plugins ####

```xml
<plugins>
    <plugin>
        <!--The available options are junit, testng, html, pretty, json, usage and rerun -->
        <name>json</name>
        <!--Optional file extension. For build in cucumber plugins a sensible default is provided. -->
        <extension>json</extension>
        <!--Optional output directory. Overrides cucumberOutputDirectory. Usefull when different 
            plugins create files with the same extension-->
        <outputDirectory>${project.build.directory}/cucumber-parallel/json</outputDirectory>
    </plugin>
</plugins>
```

#### Custom Cucumber Plugins ####

```xml
<plugins>
    <plugin>
        <name>path.to.my.formaters.CustomHtmlFormatter</name>
        <!--Optional file extension. Unless the formatter writes to a file it is strongly 
            recommend that one is provided. -->
        <extension>html</extension>
        <!--Optional output directory. Overrides cucumberOutputDirectory. Useful when different 
            plugins create files with the same extension-->
        <outputDirectory>${project.build.directory}/cucumber-parallel/html</outputDirectory>
    </plugin>
</plugins>
```

#### Custom Cucumber Plugins without output ####

```xml
<plugins>
    <plugin>
        <name>path.to.my.formaters.NoOutputFormatter</name>
        <!--Set to true if this plug creates no output. Setting extension or outputDirectory 
            will override this setting -->
        <noOutput>true</noOutput>
    </plugin>
</plugins>
```

### Naming Scheme ###

The naming scheme used for the generated files is controlled by the `namingScheme` property.  The following values are supported:

| Property      | Generated Name |
| ------------- | -------------- |
| simple        | `ParallelXXIT.java`, where `XX` is a one up counter.|
| feature-title | The name is generated based on the feature title with a set of rules to ensure it is a valid classname. The reules are detailed in the next subsection below. |
| pattern       | Generate the filename based on the `namingPattern` property.|

By default, generated test files use the `simple` naming strategy.

#### Feature-title Naming Scheme - rules ####

* Spaces are removed, camel-casing the title
* If the feature file starts with a digit, the classname is prefixed with '_'
* A one up counter is appended to the classname, to prevent clashes.

#### Pattern Naming Scheme - tokens ####

The following tokens can be used in the pattern:
* `{f}` Converts the feature file name to a valid class name using the rules for feature-title, apart from the one up counter.
* `{c}` Adds a one up counter.
* `{c:n}` Adds a one up counter modulo n (useful for selecting tests for parallelisation).
* By default counter value is zero padded to two characters and modulo counter is not padded at all. If you need a different zero padded length, this can be achieved by prefixing the character `c` by the length required - for example `{3c}` will yield `001` instead of `01`

#### Note on Pattern Naming Scheme ####
The `pattern` naming scheme is for advanced usage only.  

It is up to you to ensure that class names generated are valid and there are no clashes.  If the same class name is generated multiple times, then it shall be overwritten and some of your tests will not be executed.

The `namingPattern` property is for the **class name** only.  Do not add the `.java` suffix.

### Custom Templates ###

Some reporting plugins, such as
[Cucumber Extent Reporter](https://github.com/email2vimalraj/CucumberExtentReporter), require some 
setup before a test is started. A template can be used to customize the integration tests. For a 
sample see the [extents-report](src/it/junit/extents-report) integration test. For a full list of 
available variables please see CucumberITGeneratorBy(Feature|Scenario).

FAQ
===
Q. Why are my tests not executed?

A. By default this plugin generates integration tests. Ensure that the 
[Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/) is properly 
  configured.
  
Q. Why am I not seeing any generated runners?

A. Scenarios that don't match any tags are excluded. If there is no scenario to run, no runner will 
   be generated.
   
Q. Is there a mailing list?

A. No. but we have a Gitter channel: https://gitter.im/cucumber-jvm-parallel-plugin/

Migration
=========

Migrating from a previous version of the cucumber-jvm-parallel-plugin?

Please read the [Migration Notes](./migration.md).


Changelog
=========

* [Changelog](./changelog.md)

Contributing
============

To contribute:

* Create an integration test to demonstrate the behaviour under `src/it/`.  For example, to add support for multiple output formats for junit runners:
    * Create `src/it/junit/multiple-format`
    * copy the contents of the `src/it/junit/simple-it` directory and update the `pom/src` as appropriate to demonstrate the configuration.  Update `verify.groovy` to implement the test for your feature.
    * Run `mvn clean install -Prun-its` to run the integration tests.
* Implement the feature
* When all tests are passing, submit a pull request.
* Coding standards, based on the [Google Style Guide](https://github.com/google/styleguide) are enforced using Checkstyle.  Formatters for Eclipse and IntelliJ are provided in the `doc/style` folder

Once the pull request has been merged, a new release will be performed as soon as practicable.

Release Process
---------------
See [Maven Central upload guide](https://maven.apache.org/guides/mini/guide-central-repository-upload.html)
