package com.k_int.handlerregistry

class HandlerRevision {

  Handler owner
  long revision
  String handler

  static belongsTo = [owner : Handler]

  static constraints = {
    handler(maxSize:1000000)  
  }

}
