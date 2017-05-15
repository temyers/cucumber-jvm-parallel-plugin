import org.junit.Assert

import static org.hamcrest.Matchers.equalTo

def outputPath = "target/generated-test-sources/cucumber/"
File suite01 = new File(basedir, outputPath + "Parallel01IT.java")
File suite02 = new File(basedir, outputPath + "Parallel02IT.java")

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

Assert.assertThat(suite01.text.replaceAll("\n|\r",""), equalTo(expected01.replaceAll("\n|\r","")))