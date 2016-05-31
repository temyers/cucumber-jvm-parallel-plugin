// Multiple runs should not overwrite files.
File suite01 = new File(basedir, "target/generated-test-sources/cucumber/Parallel01IT.java");
File suite02 = new File(basedir, "target/generated-test-sources/cucumber/Parallel02IT.java");
File suite03 = new File(basedir, "target/generated-test-sources/cucumber/Parallel03IT.java");
File suite04 = new File(basedir, "target/generated-test-sources/cucumber/Parallel04IT.java");

assert suite01.isFile()
assert suite02.isFile()
assert suite03.isFile()
assert suite04.isFile()

