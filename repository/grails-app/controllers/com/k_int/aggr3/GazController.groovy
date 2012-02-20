package com.k_int.aggr3

import grails.converters.*
import groovy.text.Template
import groovy.text.SimpleTemplateEngine
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.ApplicationHolder


class GazController {

  def gazetteerService

  def index = {
    def gazresp = gazetteerService.resolvePlaceName(params.q)
    def results = ["results":gazresp]

    if ( params.callback != null ) {
      render "${params.callback}(${results as JSON})"
    } else {
      render results as JSON
    }
  }


}
