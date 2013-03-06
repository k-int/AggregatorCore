eventCompileStart = { kind ->
    def buildNumber = metadata.'app.buildNumber'

    if (!buildNumber)
    buildNumber = 1
    else
    buildNumber = Integer.valueOf(buildNumber) + 1

    def formatter = new java.text.SimpleDateFormat("MMM dd, yyyy")
    def buildDate = formatter.format(new Date(System.currentTimeMillis()))
    metadata.'app.buildDate' = buildDate
    metadata.'app.buildProfile' = grailsEnv

    metadata.'app.buildNumber' = buildNumber.toString()

    metadata.persist()

    println "**** Compile Starting on Build #${buildNumber}"
}

eventCreateWarStart = { warName, stagingDir ->
    println "eventCreateWarStart"
    // Ant.delete(file:"${stagingDir}/WEB-INF/lib/jcl-over-slf4j-1.5.8.jar", verbose:true)
    // Ant.delete(file:"${stagingDir}/WEB-INF/lib/jcl-over-slf4j-1.6.1.jar", verbose:true)
    // Ant.delete(file:"${stagingDir}/WEB-INF/lib/slf4j-api-1.5.8.jar", verbose:true)
    // Ant.delete(file:"${stagingDir}/WEB-INF/lib/slf4j-api-1.6.1.jar", verbose:true)
    // Ant.delete(file:"${stagingDir}/WEB-INF/lib/slf4j-api-1.6.4.jar", verbose:true)
    // Ant.delete(file:"${stagingDir}/WEB-INF/lib/log4j-1.2.16.jar", verbose:true)
}

