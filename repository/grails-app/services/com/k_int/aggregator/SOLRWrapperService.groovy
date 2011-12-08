package com.k_int.aggregator

import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest.ACTION;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.codehaus.groovy.grails.commons.ApplicationHolder;

// import org.apache.solr.common.SolrInputDocument;
// import org.apache.solr.core.CoreContainer;
// import org.apache.solr.core.SolrCore;

class SOLRWrapperService {

  static transactional = false
  def application = ApplicationHolder.application;

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

  def verifyCore(corename) {
    def core_container = application.config.com.k_int.aggregator.aggregationServices.solr.default_core_name
    verifyCore(corename,core_container)
  }

  def verifyCore(corename, corecontainer) {
    log.debug("Verifying core ${corename} in container ${corecontainer}")

    def solr_core_container_config = application.config.com.k_int.aggregator.aggregationServices.solr[corecontainer]

    if ( solr_core_container_config != null ) {
      log.debug("Verify core [${corecontainer}] -> ${solr_core_container_config.name}, ${solr_core_container_config.baseUrl}")
      if ( solr_core_container_config.adminConfig != null ) {
        log.debug("Got admin config... processing");
      }
      else {
        log.error("No adminConfig for this core");
      }
    }
    else {
      log.error("Unable to verify a core as there is no core-admin properties in the system settings");
    }
    
    // def core_container = org.apache.solr.core.CoreContainer.Initializer.initialize();
    //log.debug("got core_container:${core_container}");
    //log.debug("cores: ${core_container.getCoreNames()}");
  }
}
