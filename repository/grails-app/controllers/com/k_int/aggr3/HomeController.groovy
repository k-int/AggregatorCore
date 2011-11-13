package com.k_int.aggr3

@Grab(group='com.gmongo', module='gmongo', version='0.9.2')

import com.gmongo.GMongo

class HomeController {


  def index = { 

    // Get hold of mongodb 
    def result = [:]

    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("oda")

    // Store a definition of the searchable part of the resource in mongo
    result.aggregations = [:]
    result.aggregations['es'] = db.aggregations.find(type: 'es')

    // Ideally we would dynamically look up aggregation services here and auto discover any aggregations.
    // Look up any mongo headings
    log.debug("Listing mongo collections");
    def mongo_databases = mongo.getDatabaseNames();
    // def mongo_collections = mongo.getCollectionNames();
    log.debug("Mongo databases: ${mongo_databases}");
    mongo_databases.each { md ->
      def db_colls = mongo.getDB(md);
      log.debug("colls at ${md} - ${db_colls}");
    }

    // es_aggregations.each { esa ->
    //   log.debug(esa);
    // }
    result
  }
}
