package com.k_int.handlerregistry

class HandlerRevision {

  Handler owner
  long revision
  String handler

  static constraints = {
    handler(maxSize:1000000)  
  }

}
