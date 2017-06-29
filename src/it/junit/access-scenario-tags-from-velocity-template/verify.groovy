import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

def outputPath = "target/generated-test-sources/cucumber/"
File suite01 = new File(basedir, outputPath + "Parallel01IT.java")
File suite02 = new File(basedir, outputPath + "Parallel02IT.java")
File suite03 = new File(basedir, outputPath + "Parallel03IT.java")

assert suite01.isFile()
assert suite02.isFile()
assert suite03.isFile()

String expected01 = "// This is a custom template for Parallel01IT with an array of tags [@featuretag, @scenariotag1]"
String expected02 = "// This is a custom template for Parallel02IT with an array of tags [@outline-tag, @feature-tag2, @example1]"
String expected03 = "// This is a custom template for Parallel03IT with an array of tags [@example2, @outline-tag, @feature-tag2]"

// The order of the files isn't important but listFiles may list files in any order
// This ensures we assert correctly despite file ordering
File actualSuite01;
File actualSuite02;
File actualSuite03;


if (suite01.text.contains("01IT")) {
    actualSuite01 = suite01

    if (suite02.text.contains("02IT")) {
        actualSuite02 = suite02
        actualSuite03 = suite03
    } else {
        actualSuite02 = suite03
        actualSuite03 = suite02
    }

} else if (suite01.text.contains("02IT")) {

    actualSuite01 = suite01

    if (suite02.text.contains("01IT")) {
        actualSuite02 = suite01
        actualSuite03 = suite03
    } else {
        actualSuite02 = suite03
        actualSuite03 = suite01
    }

} else {
    actualSuite01 = suite03

    if (suite02.text.contains("01IT")) {
        actualSuite02 = suite01
        actualSuite03 = suite02
    } else {
        actualSuite02 = suite02
        actualSuite03 = suite01
    }

}

Assert.assertThat(actualSuite01.text, equalToIgnoringWhiteSpace(expected01))
Assert.assertThat(actualSuite02.text, equalToIgnoringWhiteSpace(expected02))
Assert.assertThat(actualSuite03.text, equalToIgnoringWhiteSpace(expected03))



