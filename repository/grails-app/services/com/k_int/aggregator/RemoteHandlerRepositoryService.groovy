package com.k_int.aggregator

import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.converters.*
 
class RemoteHandlerRepositoryService {

    static transactional = true

    // see http://groovy.codehaus.org/modules/http-builder/doc/rest.html

    def findHandlerWhen(props) {

      def result = null;

      log.debug("Finding any remote handlers for properties : ${props.keySet()}, remote repo configured as ${ApplicationHolder.application.config.com.k_int.aggregator.handlers.remoteRepo}");


      // def remote_repo = new RESTClient( 'http://developer.k-int.com');
      def remote_repo = new RESTClient( ApplicationHolder.application.config.com.k_int.aggregator.handlers.remoteRepo )

      def json_constraints = props as JSON

      def resp = remote_repo.post( 
                     path : '/HandlerRegistry/findWhen',
                     body : [ constraints:json_constraints?.toString() ],
                     requestContentType : URLENC )
 
      // assert resp.status == 200
      // assert ( resp.data instanceof GPathResult ) // parsed using XmlSlurper
      // assert resp.data.text == msg
      // assert resp.data.user.screen_name == userName
      // def postID = resp.data.id.toInteger()

   
      log.debug("Result of findWhen on remote repository: resp.status=${resp.status}, resp.data=${resp.data}")

      if ( resp.data?.code == 0 ) {
        log.debug("Located remote handler with name ${resp.data.handlerName}, revision: ${resp.data.handler_revision}");
        log.debug("Handler: ${resp.data.handler}");
        log.debug("EventCode: ${resp.data.eventCode}");
        log.debug("Preconditions: ${resp.data.preconditions}");

        result = new ScriptletEventHandler(
          name: resp.data.handlerName,
          eventCode: resp.data.eventCode,
          preconditions: resp.data.preconditions,
          scriptlet: resp.data.handler,
          active: true
        ).save();
      }

      log.debug("Done");
      result;
    }
}
