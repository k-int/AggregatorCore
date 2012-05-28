package com.k_int.aggregator

class ControlledVocabulary {

  String shortcode
  String name
  String identifier

  static constraints = {
    shortcode(nullable:false, blank:false)
    name(nullable:false, blank:false)
    identifier(nullable:false, blank:false)
  }
}
