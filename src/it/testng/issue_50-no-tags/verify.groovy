import org.junit.Assert;

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/Parallel03IT.java");

assert suite01.isFile()
assert suite02.isFile()
assert !suite03.isFile()

String noTags = "tags = {}"
String expected01 = "feature1.feature"
String expected02 = "feature2.feature"

Assert.assertTrue(suite01.text.contains(noTags))
Assert.assertTrue(suite02.text.contains(noTags))
if (suite01.text.contains(expected01)) {
    Assert.assertTrue(suite02.text.contains(expected02))
} else {
    Assert.assertTrue(suite01.text.contains(expected02))
    Assert.assertTrue(suite02.text.contains(expected01))
}
	


