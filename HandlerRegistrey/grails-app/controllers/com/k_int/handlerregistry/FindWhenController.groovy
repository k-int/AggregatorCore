package com.k_int.handlerregistry

import grails.converters.*

class FindWhenController {

  def index = { 
    log.debug("action called with stringified json constraints: ${params.constraints}");

    def constraints = JSON.parse(params.constraints)

    log.debug("${constraints}");
  }
  
}
