package com.k_int.handlerregistry

class HandlerRevision {

  Handler owner
  long revision
  String handler
  String[] preconditions

  static belongsTo = [owner : Handler]
  static hasMany = [preconditions:String]


  static constraints = {
    handler(maxSize:1000000)  
    preconditions joinTable:[name:'handler_rev_preconditions', key:'handler_rev_id', column:'precondition', type:"text"]  
  }

}
