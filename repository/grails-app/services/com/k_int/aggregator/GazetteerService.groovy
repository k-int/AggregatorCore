package com.k_int.aggregator

class GazetteerService {

  def ESWrapperService

  def resolvePlaceName(esclient, query_input) {

    def esnode = ESWrapperService.getNode();
    GClient esclient = esnode.getClient();

    def gazresp = [:]
    gazresp.places = []
    gazresp.newq = "";

    try {
      println "Resolve place name in ${query_input}"
  
      // Step 1 : See if the input place name matches a fully qualified place name
      println "exact match q params: ${query_input}"
  
      def result = search(esclient, "fqn.orig:\"${query_input}\"", 0, 10);
  
      if ( result.response.hits.totalHits == 1 ) {
        System.out.println("Exact match on fqn for ${query_input}");
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
        System.out.println("No exact fqn match for ${query_input}, try sub match");
        result = search(esclient, "fqn:\"${query_input}\"", 0, 10);
        System.out.println("Got ${result.response.hits} hits...");
        if ( result.response.hits.totalHits > 0 ) {
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
    catch( Exception e ) {
      log.error("Problem geocoding",e);
    }
    finally {
      esclient.close()
    }

    gazresp
  }

  def search(esclient, qry, start, rows) {

    println("Search for ${qry}");

    def search_closure = {
      source {
        from = 0
        size = 10
        query {
          query_string (query: qry)
        }
        sort = [
          type order = 'desc'
        ]
      }
    }

}
