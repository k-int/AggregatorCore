package com.k_int.aggregator

class DefaultUploadEventHandlerService {

    static transactional = true

    @javax.annotation.PostConstruct
    def init() {
      println "Initialising default upload handlers ${this.hashCode()}"
    }

    def handleUnknown(props) {
      println "handleUnknown"
    }

    def handleXML(props) {
      println "handleXML content_type ${props.content_type}"

      // def xml_txt = props.file.inputStream.text
      // def root = new XmlParser().parseText(props.file.inputStream)
      def xml = new XmlSlurper().parse(props.file.inputStream)

      def root_element_namespace = xml.namespaceURI();
      def root_element_name = xml.name();



      // Root node information....
      println "Root element namespace: ${root_element_namespace} root element: ${root_element_name}"
    }
}
