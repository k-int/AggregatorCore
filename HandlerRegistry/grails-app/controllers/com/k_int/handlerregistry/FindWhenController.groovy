package com.k_int.handlerregistry

import grails.converters.*

class FindWhenController {

  def index = { 
    log.debug("action called with stringified json constraints: ${params.constraints}");

    if ( params.constraints?.length() > 0 ) {
      log.debug("${params.constraints} - attempting json parse");
      def constraints = JSON.parse(params.constraints)
    }
    else {
      log.error("No constraints in request");
    }

    def result = [:]

    render result as JSON
  }
  
}
