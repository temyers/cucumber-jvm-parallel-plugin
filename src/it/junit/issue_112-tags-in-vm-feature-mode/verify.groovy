import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java")
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java")

assert suite01.isFile()
assert !suite02.isFile()

String expected01 = 
"""
/*
The feature tags are:
    @featureTag
    @myOtherFeatureTag(\\"Value\\")

The scenario definition tags are:

*/
"""

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expected01))