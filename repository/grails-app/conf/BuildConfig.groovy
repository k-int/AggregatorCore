grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache' 
        // /usr/local/grails-1.3.7/src/java/org/codehaus/groovy/grails/resolve/IvyDependencyManager.groovy
        // 'org.slf4j:slf4j-api:1.5.8', 
        // 'org.slf4j:slf4j-log4j12:1.5.8', 
        // 'org.slf4j:jcl-over-slf4j:1.5.8'
        // excludes "slf4j-api",
        //          "slf4j-log4j12",
        //          "jcl-over-slf4j"
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        inherits true
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        // mavenRepo "http://repository.codehaus.org"
        mavenRepo "https://oss.sonatype.org/content/repositories/releases"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://repo1.maven.org/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        // compile 'org.codehaus.groovy:groovy-all:1.8.1'
        runtime 'mysql:mysql-connector-java:5.1.18'
        runtime 'com.gmongo:gmongo:1.0'
        runtime 'org.elasticsearch:elasticsearch-lang-groovy:1.1.0'
        runtime 'org.apache.tika:tika-core:0.9'
        // runtime (group:'org.apache.solr',name:'solr-solrj',version:'3.5.0', transitive:false)  // This seems to work!
        runtime (group:'org.apache.solr',name:'solr-solrj',version:'3.5.0') {
            excludes([group:'org.slf4j',name:'slf4j-api',version:'1.5.8'],
                [group:'org.slf4j',name:'jcl-over-slf4j',version:'1.5.8'],
                [group:'org.slf4j',name:'slf4j-log4j12',version:'1.5.8'])
        }

        //  Special dependencies because of solrj dep conflicts.
        // runtime 'org.slf4j:slf4j-api:1.6.4'
        // runtime 'org.slf4j:slf4j-log4j12:1.6.4'
        // runtime 'org.slf4j:jcl-over-slf4j:1.6.4'

        // runtime 'org.apache.tika:tika-parsers:0.9' // this is for actually parsing files
    }
    plugins {
        compile ":google-visualization:0.6.1"
        runtime ":hibernate:$grailsVersion"
        build ":tomcat:$grailsVersion"
        //   compile ":resources:1.0 > *"
    }


}
