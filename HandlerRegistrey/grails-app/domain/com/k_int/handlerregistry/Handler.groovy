package com.k_int.handlerregistry

class Handler {

  String name
  List revisions = []

  static hasMany = [  revisions : HandlerRevision ]

  static constraints = {
  }
}
