def logFile = new File(basedir, 'build.log')
content = logFile.text

assert content.contains('Features directory is empty. No runners will be generated')


def directory = new File(basedir, "target/generated-test-sources/cucumber")

assert directory.isDirectory()
assert directory.listFiles().length==0