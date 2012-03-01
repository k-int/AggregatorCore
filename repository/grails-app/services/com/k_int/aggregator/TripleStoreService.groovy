package com.k_int.aggregator

import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import grails.converters.*
import java.security.MessageDigest


class TripleStoreService {

  def fourstore_endpoint = null;
  
  @javax.annotation.PostConstruct
  def init() {
    log.debug("init...");
    fourstore_endpoint = new HTTPBuilder( 'http://localhost:9000/' )
    // fourstore_endpoint.auth.basic feed_definition.target.identity, feed_definition.target.credentials
    // this.updateURL = new URL(baseurl + "/data/");
  }

  def update(graph, graph_uri, mimetype) { // throws MalformedURLException, ProtocolException, IOException {

    try {
      log.debug("update ${graph_uri} - upload graph ${graph}");

      fourstore_endpoint.request(POST) { request ->
        // requestContentType = 'multipart/form-data'
        requestContentType = 'application/x-www-form-urlencoded'

        uri.path = 'data/'

        body =  [ 
          'mime-type' : 'mimetype' , 
          'graph' : graph_uri,
          'data' : graph
          // 'graph' : URLEncoder.encode(graph_uri, "UTF-8"),
          // 'data' : URLEncoder.encode(graph, "UTF-8")
        ] 

        response.success = { resp, data ->
          log.debug("response status: ${resp.statusLine} ${data}")
        }

        response.failure = { resp ->
          log.error("Failure - ${resp.statusLine} ${resp}");
        }
      }
    }
    catch ( Exception e ) {
      log.error("Error updating triple store",e);
    }
    finally {
      log.debug("Complete");
    }
  }

  def removeGraph(graph_uri) {
    // URL delete_url = new URL(baseurl + "/data/"+uri);
    // HttpURLConnection connection = (HttpURLConnection) this.updateURL.openConnection();
    // connection.setDoOutput(true);
    // connection.setRequestMethod("DELETE");
    // connection.connect();
  }
}
