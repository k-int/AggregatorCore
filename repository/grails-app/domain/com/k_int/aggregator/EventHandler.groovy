package com.k_int.aggregator

class EventHandler {

  String name
  String eventCode
  boolean active = false
  String[] preconditions
  Date installDate = new java.util.Date()

  static constraints = {
    eventCode(maxSize:1000000)
  }
}
