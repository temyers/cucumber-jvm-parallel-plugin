import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java")
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java")
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/Parallel03IT.java")

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

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expected02))

