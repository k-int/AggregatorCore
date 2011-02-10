package com.k_int.aggregator

class HandlerSelectionService {

    static transactional = true

    def selectHandlersFor(base_event, properties) {
      println "Locating handlers for ${base_event}, ${properties}"
    }
}
