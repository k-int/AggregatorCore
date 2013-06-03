grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://oss.sonatype.org/content/repositories/releases"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // The following dependencies are listed because GroovyClassLoader.parseClass is, for some reason,
        // not reading annotations when loading classes under grails run-war, although everything seems fine
        // when running with grails run-app
        //
        // For now, we make sure the deps are available at compile time so we aren't reliant upon the @grape @grab
        // The downside is plugins can only access jars that are imported here.

        compile 'org.codehaus.groovy:groovy-all:1.8.1'
        runtime 'mysql:mysql-connector-java:5.1.25'
        runtime 'com.gmongo:gmongo:1.2'
        runtime 'org.elasticsearch:elasticsearch-lang-groovy:1.2.0'
        runtime 'org.apache.tika:tika-core:0.9'
        // runtime (group:'org.apache.solr',name:'solr-solrj',version:'3.5.0', transitive:false)  // This seems to work!
        runtime (group:'org.apache.solr',name:'solr-solrj',version:'3.5.0') {
            excludes([group:'org.slf4j',name:'slf4j-api',version:'1.5.8'],
                [group:'org.slf4j',name:'jcl-over-slf4j',version:'1.5.8'],
                [group:'org.slf4j',name:'slf4j-log4j12',version:'1.5.8'])
        }
    }
    plugins {
        runtime ":hibernate:$grailsVersion"
        build ":tomcat:$grailsVersion"
    }
}
