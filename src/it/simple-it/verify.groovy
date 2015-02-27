import org.junit.Assert;

File suite01 = new File( basedir, "target/generated-test-sources/cucumber/Parallel01IT.java" );
File suite02 = new File( basedir, "target/generated-test-sources/cucumber/Parallel02IT.java" );

assert suite01.isFile()
assert suite02.isFile()

// Windows/Linux list files differently
String firstFeature = File.separatorChar == '/' ? "feature2" : "feature1"
String secondFeature = File.separatorChar == '/' ? "feature1" : "feature2"


String expected01=
"""import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, features = {"classpath:features/${firstFeature}.feature"}, format = {"json:target/cucumber-parallel/1.json",
"pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Parallel01IT {
}"""

Assert.assertEquals(expected01.replace("\\r\\n","\\n"),suite01.text.replace("\\r\\n","\\n"))


String expected02=
"""import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, features = {"classpath:features/${secondFeature}.feature"}, format = {"json:target/cucumber-parallel/2.json",
"pretty"}, monochrome = false, tags = {"@complete", "@accepted"}, glue = { "foo", "bar" })
public class Parallel02IT {
}"""

Assert.assertEquals(expected02.replace("\\r\\n","\\n"),suite02.text.replace("\\r\\n","\\n"))
