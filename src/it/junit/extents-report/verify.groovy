import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File buildDirectory = new File(basedir, "target");

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");

File feature1 = new File(basedir, "/src/test/resources/features/feature1.feature");
File feature2 = new File(basedir, "/src/test/resources/features/feature2.feature");

assert suite01.isFile()
assert suite02.isFile()

String expected01 =
        """import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import com.cucumber.listener.Reporter;

import java.io.File;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature1.absolutePath}"},
        plugin = {"com.cucumber.listener.ExtentCucumberFormatter:${buildDirectory.absolutePath}/cucumber-parallel/1.html"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class Parallel01IT {

    @BeforeClass
    public static void beforeClass(){
      //Do things
    }

    @AfterClass
    public static void afterClass(){
      Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
      Reporter.setSystemInfo("user", System.getProperty("user.name"));
      Reporter.setSystemInfo("os", "Mac OSX");
      Reporter.setTestRunnerOutput("Sample test runner output message");
    }
}"""

String expected02 =
        """import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import com.cucumber.listener.Reporter;

import java.io.File;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"${feature2.absolutePath}"},
        plugin = {"com.cucumber.listener.ExtentCucumberFormatter:${buildDirectory.absolutePath}/cucumber-parallel/2.html"},
        monochrome = false,
        tags = {},
        glue = {"foo", "bar"})
public class Parallel02IT {

    @BeforeClass
    public static void beforeClass(){
      //Do things
    }

    @AfterClass
    public static void afterClass(){
      Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
      Reporter.setSystemInfo("user", System.getProperty("user.name"));
      Reporter.setSystemInfo("os", "Mac OSX");
      Reporter.setTestRunnerOutput("Sample test runner output message");
    }
}"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))

