package com.k_int.aggregator

class Resource {

  DataProvider owner
  String identifier
  String title
  String status
  DepositEvent latestDeposit
  Date appLastMod


  static constraints = {
    title(blank:true,nullable:true)
  }
}
