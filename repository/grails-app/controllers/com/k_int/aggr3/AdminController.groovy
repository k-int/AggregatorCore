package com.k_int.aggr3

import com.gmongo.GMongo
import com.k_int.aggregator.*
import grails.plugins.springsecurity.Secured

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class AdminController {

  def remoteHandlerRepositoryService
  def handlerSelectionService
  def HandlerExecutionService

  def index = {
    // Get hold of mongodb 
    def result = [:]
    getDataForAdminHomePage(result)
    result
  }

  def clearHandlers = {
    flash.clear()
    handlerSelectionService.clearDown();
    HandlerExecutionService.clearDown();
    flash.message = "handlers.clear.message"
    // flash.args = []
    flash.default = "Handlers Cleared Down."
    redirect(action:'index')
    // render(view:'index', model:result)
  }

  def getDataForAdminHomePage(result) {

    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("oda")

    // Store a definition of the searchable part of the resource in mongo
    result.aggregations = [:]
    result.aggregations['es'] = db.aggregations.find(type: 'es')
    result.aggregations['mongo'] = []

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

    result.handlers = EventHandler.list()

    result.handler_cache = handlerExecutionService.handler_cache

    // es_aggregations.each { esa ->
    //   log.debug(esa);
    // }

    result.handlerrepos = []
    result.handlerrepos.add( [
                              url:remoteHandlerRepositoryService.remote_repo_url,
                              user:remoteHandlerRepositoryService.remote_user])
    result.sysid = remoteHandlerRepositoryService.sys_id
  }
}
