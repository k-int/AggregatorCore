package com.k_int.handlerregistry

class HandlerRevision {

  Handler owner
  long revision
  String handlerText

  static constraints = {
    handlerText(maxSize:1000000)  
  }

}
