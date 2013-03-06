package com.k_int.handlerregistry

class Handler {

    String name
    String[] preconditions

    // static hasMany = [  preconditions : String ]

    static constraints = {
        // preconditions joinTable:[name:'handler_preconditions', key:'handler_id', column:'precondition', type:"text"]
    }

}
