import org.junit.Assert

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/Parallel03IT.java");
File suite04 = new File(basedir, "target/generated-test-sources/cucumber/Parallel04IT.java");

File expectedSuite01 = new File( basedir, "src/test/resources/expectedFiles/Parallel01IT.java" );
File expectedSuite02 = new File( basedir, "src/test/resources/expectedFiles/Parallel02IT.java" );
File expectedSuite03 = new File( basedir, "src/test/resources/expectedFiles/Parallel03IT.java" );
File expectedSuite04 = new File( basedir, "src/test/resources/expectedFiles/Parallel04IT.java" );


assert suite01.isFile()
assert suite02.isFile()
assert suite03.isFile()
assert suite04.isFile()

Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expectedSuite01.text))
Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expectedSuite02.text))
Assert.assertThat(suite03.text, equalToIgnoringWhiteSpace(expectedSuite03.text))
Assert.assertThat(suite04.text, equalToIgnoringWhiteSpace(expectedSuite04.text))





