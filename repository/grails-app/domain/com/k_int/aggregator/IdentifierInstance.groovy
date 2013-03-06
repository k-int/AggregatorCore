package com.k_int.aggregator

class IdentifierInstance {


    CanonicalIdentifier owner

    String identifierType // Undefined or some other type identifier / context / namespace [canonical for canonical guid]
    String identifierValue

    static constraints = {
    }
}
