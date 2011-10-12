package com.k_int.repository.handlers

@Grab(group='com.gmongo', module='gmongo', version='0.5.1')

import com.gmongo.GMongo
import org.apache.commons.logging.LogFactory



class XCRIHandler {

  private static final log = LogFactory.getLog(this)

  // handlers have access to the repository mongo service.. suggest you use http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/
  def getHandlerName() {
    "XCRI_CAP"
  }

  def getRevision() {
    1
  }

  def getPreconditions() {
    [
      'p.rootElementNamespace=="http://xcri.org/profiles/catalog"'
    ]
  }

  def process(props, ctx) {
    log.debug("this is a doodah");
    props.response.messageLog.add("This is a message from the downloaded XCRI handler")

    def d2 = props.xml.declareNamespace(['xcri':'http://xcri.org/profiles/catalog']) // , ns2: 'http://www.example.org/NS2') 

    // def xcri = new groovy.xml.Namespace("http://xcri.org/profiles/catalog", 'xcri') 

    log.debug("root element namespace: ${d2.namespaceURI()}");
    log.debug("lookup namespace: ${d2.lookupNamespace('xcri')}");
    log.debug("d2.name : ${d2.name()}");

    // Properties contain an xml element, which is the parsed document
    def id1 = d2.'xcri:provider'.'xcri:identifier'.text()

    props.response.messageLog.add("Identifier for this XCRI document: ${id1}")

  }
}
