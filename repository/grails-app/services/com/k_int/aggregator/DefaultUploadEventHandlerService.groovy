package com.k_int.aggregator

class DefaultUploadEventHandlerService {

    static transactional = true

    @javax.annotation.PostConstruct
    def init() {
      println "Initialising default upload handlers ${this.hashCode()}"
    }

    def handleUnknown(properties, file) {
      println "handleUnknown"
    }

    def handleXML(properties, file) {
      println "handleXML"
    }
}
