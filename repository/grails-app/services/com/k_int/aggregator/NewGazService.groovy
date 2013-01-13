package com.k_int.aggregator

import static groovyx.net.http.Method.*
import groovyx.net.http.RESTClient
import groovyx.net.http.*

class NewGazService {

  def mongoService
  def geocode_count = 0;

  def geocode(address) {
    def gazcache_db = mongoService.getMongo().getDB("googlegazcache")
    def geo_result = gazcache_db.entries.findOne(address:address)
    if ( !geo_result ) {
      log.debug("No cache hit for ${address}, lookup");
      geo_result = googleGeocode(address, gazcache_db);
    }

    def result = geo_result.response.results[0]

    result
  }

  def googleGeocode(postcode, gazcache_db) {

    def searches = ["administrative_area_level_2","postal_code_prefix","route","locality"]
    def geoLocation = ['county','postcode','street','locality']
    def result =[:]
    def http = new HTTPBuilder("http://maps.googleapis.com");
    http.request(Method.valueOf("GET"), ContentType.JSON) {
      uri.path = '/maps/api/geocode/json'
      uri.query = [ 'address' : "$postcode", 'sensor' : 'false' ]
      response.success = {resp, json ->

        def cache_entry = [ address:postcode,
                            response:json,
                            lastSeen: System.currentTimeMillis(),
                            created: System.currentTimeMillis() ]
  
        gazcache_db.entries.save(cache_entry);
      }
    }

    geocode_count++

    result
  }
}
