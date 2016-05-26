def logFile = new File(basedir, 'build.log')
content = logFile.text

assert content.contains('Features directory does not exist')
