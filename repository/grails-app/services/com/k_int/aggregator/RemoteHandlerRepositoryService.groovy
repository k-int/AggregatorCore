package com.k_int.aggregator

import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC
 
class RemoteHandlerRepositoryService {

    static transactional = true

    // see http://groovy.codehaus.org/modules/http-builder/doc/rest.html

    def findHandlerWhen(props) {
      log.debug("Finding any remote handlers for properties : ${props.keySet()}");

      def remote_repo = new RESTClient( 'http://developer.k-int.com');

      def resp = remote_repo.post( 
                     path : 'findWhen',
                     body : [ status:'doobrie', 
                              source:'httpbuilder' ],
                     requestContentType : URLENC )
 
      // assert resp.status == 200
      // assert ( resp.data instanceof GPathResult ) // parsed using XmlSlurper
      // assert resp.data.text == msg
      // assert resp.data.user.screen_name == userName
      // def postID = resp.data.id.toInteger()


      log.debug("Result of findWhen on remote repository: ${resp.status}")

      log.debug("Done");
    }
}
