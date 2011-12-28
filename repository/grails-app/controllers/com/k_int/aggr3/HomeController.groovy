package com.k_int.aggr3

@Grab(group='com.gmongo', module='gmongo', version='0.9.2')

import com.gmongo.GMongo
import com.k_int.aggregator.*

class HomeController {

  def remoteHandlerRepositoryService

  def index = { 

    // Get hold of mongodb 
    def result = [:]

    log.debug("Get database links")
    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("oda")

    // Store a definition of the searchable part of the resource in mongo
    result.aggregations = [:]
    result.aggregations['es'] = db.aggregations.find(type: 'es')
    result.aggregations['mongo'] = []

    log.debug("List mongo collections")

    // Ideally we would dynamically look up aggregation services here and auto discover any aggregations.
    // Look up any mongo headings
    log.debug("Listing mongo collections");
    def mongo_databases = mongo.getDatabaseNames();
    // def mongo_collections = mongo.getCollectionNames();
    log.debug("Mongo databases: ${mongo_databases}");
    mongo_databases.each { md ->
      def db_colls = mongo.getDB(md);
      // log.debug("colls at ${md} - ${db_colls}");
      db_colls.each { mongo_coll ->
        result.aggregations['mongo'].add([title:mongo_coll,identifier:mongo_coll,description:mongo_coll])
      }
    }

    log.debug("Listing event handlers")
    result.handlers = EventHandler.list()
    
    // es_aggregations.each { esa ->
    //   log.debug(esa);
    // }

    result.handlerrepos = []
    result.handlerrepos.add( [
                              url:remoteHandlerRepositoryService.remote_repo,
                              user:remoteHandlerRepositoryService.remote_user])
    result.sysid = remoteHandlerRepositoryService.sys_id

    result
  }
}
