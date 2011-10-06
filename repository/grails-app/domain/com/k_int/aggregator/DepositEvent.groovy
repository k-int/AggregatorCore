package com.k_int.aggregator

class DepositEvent {

  String depositToken
  String status
  Date dateCreated

  DataProvider dataProvider

  static constraints = {
    depositToken(blank:false,nullable:false)
    status(blank:false,nullable:false)
    dateCreated(blank:false, nullable:true)
    dataProvider(blank:false, nullable:true)
  }
}
