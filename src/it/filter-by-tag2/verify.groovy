import org.junit.Assert;

File suite01 = new File( basedir, "target/generated-test-sources/cucumber/Parallel01IT.java" );

// No files should be generated
assert !suite01.isFile()

