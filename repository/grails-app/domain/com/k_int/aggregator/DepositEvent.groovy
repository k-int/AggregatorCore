package com.k_int.aggregator

class DepositEvent {

  String depositToken
  String status
  Date dateCreated
  String checksum

  DataProvider dataProvider
  Resource relatedResource

  static constraints = {
    depositToken(blank:false,nullable:false)
    status(blank:false,nullable:false)
    dateCreated(blank:false, nullable:true)
    dataProvider(blank:false, nullable:true)
    relatedResource(blank:false, nullable:true)
    checksum(blank:false, nullable:true)
  }
}
