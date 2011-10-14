package com.k_int.aggregator

class ESWrapperService {

  static transactional = true

  def gNode = null;

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
    def nodeBuilder = new org.elasticsearch.groovy.node.GNodeBuilder()
    nodeBuilder.settings {
      node {
        client = true
        local = false
      }
      cluster {
        name = 'aggr'
      }
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
    log.debug("gNode");
    gNode
  }

}
