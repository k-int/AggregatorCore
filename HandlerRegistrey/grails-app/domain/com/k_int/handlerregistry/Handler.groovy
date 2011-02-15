package com.k_int.handlerregistry

class Handler {

  String name
  List revisions = []
  List preconditions = []
  HandlerRevision liveRevision

  static hasMany = [  revisions : HandlerRevision, preconditions : String ]

  static constraints = {
    preconditions joinTable:[name:'handler_preconditions', key:'handler_id', column:'precondition', type:"text"]
  }
}
