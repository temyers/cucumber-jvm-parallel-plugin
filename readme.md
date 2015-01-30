cucumber-jvm-parallel-plugin
============================

A common approach for running Cucumber features in parallel is to create a suite of Cucumber JUnit runners, one for each suite of tests you wish to run in parallel.  For maximum parallelism, there should be a runner per feature file.

This is a pain to maintain and not very DRY.

This is where the cucumber-jvm-parallel-plugin comes in.  This plugin automatically generates a Cucumber JUnit runner for each feature file found in your project.

Usage
-----

Add the following to your POM file:

```xml
<plugin>
  <groupId>com.github.temyers</groupId>
  <artifactId>cucumber-jvm-parallel-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <executions>
    <execution>
      <id>generateRunners</id>
      <phase>validate</phase>
      <goals>
        <goal>generateRunners</goal>
      </goals>
      <configuration>
          <!-- Mandatory -->
         <glue>foo, bar</glue>
         <!-- These are the default values -->
           <outputDirectory>${project.build.directory}/generated-test-sources/cucumber</outputDirectory>
          <featuresDirectory>src/test/resources/features/</featuresDirectory>
         <format>json</format>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Where glue is a comma separated list of package names to use for the Cucumber Glue.

The plugin will search `featuresDirectory` for `*.feature` files and generate a JUnit test for each one.

The Java source is generated in `outputDirectory`, and will have the pattern `ParallelXXIT.java`, where `XX` is a one up counter.

Each JUnit test is configured to output the results to a separate output file under `target/cucumber-parallel`

