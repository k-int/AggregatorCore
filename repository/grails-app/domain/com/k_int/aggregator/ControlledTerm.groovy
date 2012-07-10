package com.k_int.aggregator

class ControlledTerm {

  ControlledVocabulary owner;
  String term
  String normterm
  String identifier

  static constraints = {
    owner(nullable:false, blank:false)
    term(nullable:false, blank:false)
    normterm(nullable:false, blank:false)
    identifier(nullable:false, blank:false)
  }
}
