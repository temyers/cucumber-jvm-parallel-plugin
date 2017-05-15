import org.junit.Assert

import static org.hamcrest.Matchers.equalTo

def outputPath = "target/generated-test-sources/cucumber/"
File suite01 = new File(basedir, outputPath + "Parallel01IT.java")
File suite02 = new File(basedir, outputPath + "Parallel02IT.java")
File suite03 = new File(basedir, outputPath + "Parallel03IT.java")

assert suite01.isFile()
assert suite02.isFile()
assert suite03.isFile()

String expected01 = 
"""
/*
The feature tags are:
    @featureTag
    @myOtherFeatureTag(\\"Value\\")

The scenario definition tags are:
    @scenarioTag1
    @myOtherScenarioTag(\\"Value\\")

*/
"""

String expected02 = 
"""
/*
The feature tags are:
    @featureTag
    @myOtherFeatureTag(\\"Value\\")

The scenario definition tags are:
    @outlineTag
    @myOtherScenarioOutlineTag(\\"Value\\")

*/
"""

// The order of the files isn't important but listFiles may list files in any order
// This ensures we assert correctly despite file ordering
if (suite01.text.contains("@scenarioTag1")) {
    Assert.assertThat(suite01.text.replaceAll("\n|\r",""), equalTo(expected01.replaceAll("\n|\r","")))
    Assert.assertThat(suite02.text.replaceAll("\n|\r",""), equalTo(expected02.replaceAll("\n|\r","")))
} else {
    Assert.assertThat(suite02.text.replaceAll("\n|\r",""), equalTo(expected01.replaceAll("\n|\r","")))
    Assert.assertThat(suite01.text.replaceAll("\n|\r",""), equalTo(expected02.replaceAll("\n|\r","")))
    
}

