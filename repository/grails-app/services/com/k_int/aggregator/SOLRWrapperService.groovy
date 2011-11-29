package com.k_int.aggregator



class SOLRWrapperService {

  static transactional = false

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
    log.debug("Init completed");
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
    log.debug("Destroy completed");
  }

}
