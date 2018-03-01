import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target")

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature1Counter1Mod00Again0001Mod00000.java")
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature2Counter2Mod01Again0002Mod00001.java")
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature3Counter3Mod02Again0003Mod00002.java")
File suite04 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature4Counter4Mod00Again0004Mod00003.java")
File suite05 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature5Counter5Mod01Again0005Mod00004.java")
File suite06 = new File(basedir, "target/generated-test-sources/cucumber/FooFeature6Counter6Mod02Again0006Mod00005.java")

File feature1 = new File(basedir, "/src/test/resources/features/feature1.feature")
File feature2 = new File(basedir, "/src/test/resources/features/feature2.feature")
File feature3 = new File(basedir, "/src/test/resources/features/feature3.feature")
File feature4 = new File(basedir, "/src/test/resources/features/feature4.feature")
File feature5 = new File(basedir, "/src/test/resources/features/feature5.feature")
File feature6 = new File(basedir, "/src/test/resources/features/feature6.feature")

assert suite01.isFile()
assert suite02.isFile()
assert suite03.isFile()
assert suite04.isFile()
assert suite05.isFile()
assert suite06.isFile()

String expected01 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature1.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/1.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature1Counter1Mod00Again0001Mod00000 {
}"""

String expected02 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature2.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/2.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature2Counter2Mod01Again0002Mod00001 {
}"""

String expected03 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature3.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/3.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature3Counter3Mod02Again0003Mod00002 {
}"""

String expected04 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature4.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/4.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature4Counter4Mod00Again0004Mod00003 {
}"""

String expected05 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature5.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/5.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature5Counter5Mod01Again0005Mod00004 {
}"""

String expected06 =
        """import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature6.absolutePath}"},
        plugin = {"json:${buildDirectory.absolutePath}/cucumber-parallel/6.json"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class FooFeature6Counter6Mod02Again0006Mod00005 {
}"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))
Assert.assertThat(suite03.text, equalToIgnoringWhiteSpace(expected03))
Assert.assertThat(suite04.text, equalToIgnoringWhiteSpace(expected04))
Assert.assertThat(suite05.text, equalToIgnoringWhiteSpace(expected05))
Assert.assertThat(suite06.text, equalToIgnoringWhiteSpace(expected06))
