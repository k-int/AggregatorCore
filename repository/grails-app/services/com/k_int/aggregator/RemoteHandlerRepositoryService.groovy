package com.k_int.aggregator

import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.converters.*
import org.springframework.context.*
 
class RemoteHandlerRepositoryService implements ApplicationContextAware {

    ApplicationContext applicationContext

    def handlerExecutionService
    def sys_id = null
    def remote_repo_url = null
    def remote_user = null
    def remote_pass = null

    @javax.annotation.PostConstruct
    def init() {
      log.debug("Initialising Remote Handler Repository Service");
      sys_id = Setting.findByStKey('instanceid')?.stValue
      remote_repo_url = Setting.findByStKey('url')?.stValue
      remote_user = Setting.findByStKey('user')?.stValue
      remote_pass = Setting.findByStKey('pass')?.stValue
      log.debug("At startup, this system is identified by repository id ${sys_id}. Remote repo is ${remote_repo_url}, user at remote repo is ${remote_user}/${remote_pass}")
    }

    def findHandlerWhen(props) {

      def result = null;

      log.debug("Finding any remote handlers for properties : ${props.keySet()}, remote repo configured as ${remote_repo_url}");

      def remote_repo = new RESTClient( remote_repo_url )
      remote_repo.auth.basic remote_user, remote_pass

      def json_constraints = props as JSON

      def resp = remote_repo.post( 
                     path : '/HandlerRegistry/findWhen',
                     body : [ constraints:json_constraints?.toString(),
                              remote_instance_id:sys_id ],
                     requestContentType : URLENC )
 
      // assert resp.status == 200
      // assert ( resp.data instanceof GPathResult ) // parsed using XmlSlurper
      // assert resp.data.text == msg
      // assert resp.data.user.screen_name == userName
      // def postID = resp.data.id.toInteger()

   
      log.debug("Result of findWhen on remote repository: resp.status=${resp.status}");

      if ( resp.data?.code == 0 ) {
        log.debug("** Located remote handler with name ${resp.data.handlerName}, revision: ${resp.data.handler_revision}");
        // log.debug("Handler: ${resp.data.handler}");
        log.debug("EventCode: ${resp.data.eventCode}");
        log.debug("Preconditions: ${resp.data.preconditions}");

        result = new ScriptletEventHandler(
          name: resp.data.handlerName,
          eventCode: resp.data.eventCode,
          preconditions: resp.data.preconditions,
          scriptlet: resp.data.handler,
          active: true
        )

        if ( result.save() ) {
          log.debug("Obtaining an instance of the new handler class");
          def handler_instance = handlerExecutionService.getHandlerInstance(result);
          if ( handler_instance.metaClass.respondsTo(handler_instance,"setup", applicationContext) ) {
            log.debug("New handler has a setup method");
            handler_instance.setup(applicationContext);
          }
        }
      }
      else {
        log.warn("Error processing json response...");
      }

      log.debug("Done");
      result;
    }
}
