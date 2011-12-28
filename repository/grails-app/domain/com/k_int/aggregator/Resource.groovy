package com.k_int.aggregator

class Resource {

  DataProvider owner
  String identifier
  String title

  static constraints = {
    title(blank:true,nullable:true)
  }
}
