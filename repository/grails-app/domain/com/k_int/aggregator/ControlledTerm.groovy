package com.k_int.aggregator

class ControlledTerm {

  ControlledVocabulary owner;
  String term
  String normTerm
  String identifer

  static constraints = {
    owner(nullable:false, blank:false)
    term(nullable:false, blank:false)
    normTerm(nullable:false, blank:false)
    identifier(nullable:false, blank:false)
  }
}
