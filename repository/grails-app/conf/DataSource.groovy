// dataSource {
//     pooled = true
//     driverClassName = "org.hsqldb.jdbcDriver"
//     username = "sa"
//     password = ""
// }
// hibernate {
//     cache.use_second_level_cache = true
//     cache.use_query_cache = true
//     cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
// }
// environment specific settings
environments {
    development {
        dataSource {
            driverClassName = "com.mysql.jdbc.Driver"
            dbCreate = "create-drop"           // "create"
            username = "k-int"
            password = "k-int"
            url = "jdbc:mysql://localhost/a2dev?autoReconnect=true&amp;characterEncoding=utf8"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            pooled=true
            driverClassName = "com.mysql.jdbc.Driver"
            username = "k-int"
            password = "k-int"
            url = "jdbc:mysql://localhost/Aggr3Live?autoReconnect=true&amp;characterEncoding=utf8"
            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }
        }
    }
}
