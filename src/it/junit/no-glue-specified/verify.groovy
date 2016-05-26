def logFile = new File(basedir, 'build.log')
content = logFile.text

assert content.contains("The parameters 'glue'")
assert content.contains("are missing or invalid")
