package com.k_int.aggregator

import org.elasticsearch.groovy.common.xcontent.*
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import static groovyx.net.http.ContentType.JSON

class GazetteerService {

  def ESWrapperService
  def mongoService


    def resolvePlaceName(query_input) {
        def gazresp = null;

        try {
            gazresp = doResolvePlaceName(query_input);
        }
        catch ( Exception e ) {
            log.warn("Problem resolving place name ${e}");
        }

        gazresp
    }

    def doResolvePlaceName(query_input) {

        log.debug("Resolve place name in ${query_input}")

        org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
        org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

        def gazresp = [:]
        gazresp.places = []
        gazresp.newq = "";

        try {
  
            // Step 1 : See if the input place name matches a fully qualified place name
            println "exact match q params: ${query_input}"
  
            def result = search(esclient, "fqn.orig:\"${query_input}\"", 0, 10);
  
            if ( result?.response?.hits?.totalHits == 1 ) {
                log.debug("Exact match on fqn for ${query_input}");
                def sr = [
             'lat':result.response.hits[0].source.location?.lat,
             'lon':result.response.hits[0].source.location?.lon,
             'name':result.response.hits[0].source.placeName,
             'fqn':result.response.hits[0].source.fqn,
             'type':result.response.hits[0].source.type
                ]
                gazresp.places.add(sr)
            }
            else {
                log.debug("No exact fqn match for ${query_input}, try sub match");
                result = search(esclient, "fqn:\"${query_input}\"", 0, 10);
                // result = disMaxSearch(esclient, "fqn:\"${query_input}\"", 0, 10);
                log.debug("Got ${result.response.hits} hits...");
                if ( result?.response?.hits?.totalHits > 0 ) {
                    println("Iterating hits...");
                    result.response.hits.each { hit ->
                        println("Adding ${hit.source}");
                        gazresp.places.add([
             'lat':hit.source.location?.lat,
             'lon':hit.source.location?.lon,
             'name':hit.source.placeName,
             'fqn':hit.source.fqn,
             'type':hit.source.type
                            ] )
                    }
                    println("Done Iterating hits...");
                }
            }
        }
        catch( Exception e ) {
            log.error("Problem geocoding",e);
        }
        finally {
        }

        gazresp
    }

    def search(esclient, qry, start, rows) {

        println("Search for ${qry}");

        def res = esclient.search {
            indices "gaz"
            source {
                from = 0
                size = 10
                query {
                    query_string (query: qry)
                }
                sort = [
                    pref : [
                        order : 'desc'
                    ]
                ]
            }
        }

        println "Search returned $res.response.hits.totalHits total hits"
        res

    }

    def disMaxSearch(esclient, qry, start, rows) {
        println("Search for ${qry}");

        def search_closure = {
            source {
                from = 0
                size = 10
                query {
                    dis_max {
                        queries = [
                            term {
                                fqn='hello'
                            },
                            term {
                                placeName='hello'
                            }
                        ]
                    }
                }
                sort {
                    pref {
                        order = 'desc'
                    }
                }
            }
        }

        testSearchClosure(search_closure);

        def res = esclient.search(search_closure)
        println "Search returned $res.response.hits.totalHits total hits"
        res
    }

    def testSearchClosure(c) {
        log.debug("testSearchClosure....");
        def builder = new GXContentBuilder()
        def b = builder.buildAsString(c)
        log.debug(b.toString())
    }

    def reverseGeocode(lat,lng) {
        
      def gazcache_db = mongoService.getMongo().getDB("googlegazcache")
      def result = getCacheEntry(gazcache_db,"${lat}:${lng}");

        if ( result == null ) {
          result = [:]
          def searches = ["administrative_area_level_2","postal_code_prefix","route","locality"]
          def geoLocation = ['county','postcode','street','locality']
          def http = new HTTPBuilder("http://maps.googleapis.com");
          http.request(Method.valueOf("GET"), JSON) {
            uri.path = '/maps/api/geocode/json'
            uri.query = [ 'latlng' : "$lat,$lng",
'sensor' : 'false' ]
            response.success = {resp, json ->
                if ( json.results[0].address_components ) {
                  json.results[0].address_components.each { ac ->
                      def i = 0
                      while (i<4){
                          if ( ac.types.contains(searches[i]) ) {
                              result[geoLocation[i]]="${ac.long_name}"
                          }
                          i++
                      }
                  }
                  storeCacheEntry(gazcache_db,"${lat}:${lng}",result);
                }
                else {
                  log.error("Gaz lookup has no address components: ${json}");
                }
            }
          }
        }
        result
    }

  def getCacheEntry(gazcache_db,key) {
    def result = null
    try {
      def lookup_result = gazcache_db.reversecache.findOne(key:key)
      if ( lookup_result )
        result = lookup_result.entry;
    }
    catch ( Exception e ) {
      log.debug("problem looking up reverse geocode entry for ${key}:",e);
    }

    result
    
  }

  def storeCacheEntry(gazcache_db,key,result) {
    def store = [:]
    store.key = key
    store.entry = result;
    gazcache_db.reversecache.save(store)
  }
}
