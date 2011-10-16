package com.k_int.aggregator

import org.elasticsearch.groovy.node.GNode
import org.elasticsearch.groovy.node.GNodeBuilder
import static org.elasticsearch.groovy.node.GNodeBuilder.*

class ESWrapperService {

  static transactional = true

  def gNode = null;

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");

    // System.setProperty("java.net.preferIPv4Stack","true");

    def nodeBuilder = new org.elasticsearch.groovy.node.GNodeBuilder()

    log.debug("Construct node settings");

    nodeBuilder.settings {
      node {
        client = true
        local = true
        data = false
      }
      // cluster {
      //   name = 'aggr'
      // }
      // testing...
      // transport {
      //   port = 9305
      // }
    }

    log.debug("Constructing node...");

    gNode = nodeBuilder.node()
    // def esclient = gNode.client
    log.debug("Init completed");
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
    gNode.close()
    log.debug("Destroy completed");
  }

  def getNode() {
    log.debug("getNode()");
    gNode
  }

}
