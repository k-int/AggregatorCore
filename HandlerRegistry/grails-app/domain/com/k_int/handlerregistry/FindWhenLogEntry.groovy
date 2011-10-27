package com.k_int.handlerregistry

class FindWhenLogEntry {

  Date ts = new java.util.Date()
  String remoteUserId
  String remoteSystemId
  String remoteConstraints
  long selectedRevision
  Handler selectedHandler

  static constraints = {
    remoteConstraints(maxSize:1000000)
  }
}
