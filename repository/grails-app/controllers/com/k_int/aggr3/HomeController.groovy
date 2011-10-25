package com.k_int.aggr3

class HomeController {


  def index = { 
    // Get hold of mongodb 
    def result = [:]

    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("oda")

    // Store a definition of the searchable part of the resource in mongo
    result.aggregations = [:]
    result.aggregations['es'] = db.aggregations.find(type: 'es')

    // es_aggregations.each { esa ->
    //   log.debug(esa);
    // }
    result
  }
}
