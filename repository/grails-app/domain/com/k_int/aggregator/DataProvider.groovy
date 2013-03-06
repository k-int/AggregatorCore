package com.k_int.aggregator

class DataProvider {

    String code
    String name

    static constraints = {
        name(blank:true,nullable:true)
    }
}
