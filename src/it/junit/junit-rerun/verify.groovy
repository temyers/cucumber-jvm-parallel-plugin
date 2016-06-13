import org.junit.Assert;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;


File expectedSuite01 = new File( basedir, "src/test/resources/expectedFiles/Parallel01IT.java" );
File expectedSuite02 = new File( basedir, "src/test/resources/expectedFiles/Parallel02IT.java" );

File suite01 = new File( basedir, "target/generated-test-sources/cucumber/Parallel01IT.java" );
File suite02 = new File( basedir, "target/generated-test-sources/cucumber/Parallel02IT.java" );

assert suite01.isFile()
assert suite02.isFile()



// Depending on the OS, listFiles can list files in different order.  The actual order of files isn't necessary

if(suite01.text.contains("feature1") ) {
    Assert.assertThat(suite01.text, equalToIgnoringWhiteSpace(expectedSuite01.text))
}
if(suite02.text.contains("feature2") ){
	Assert.assertThat(suite02.text, equalToIgnoringWhiteSpace(expectedSuite02.text))
}

