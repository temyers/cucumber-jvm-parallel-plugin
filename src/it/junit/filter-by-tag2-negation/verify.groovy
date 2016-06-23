import org.junit.Assert;

File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");

// No files should be generated
assert !suite01.exists()
assert !suite02.exists()



