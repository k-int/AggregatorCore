package com.k_int.handlerregistry

import grails.converters.*

class FindWhenController {

  def index = { 
    log.debug("action called with stringified json constraints: ${params.constraints}");

    def result = [code:1]

    if ( params.constraints?.length() > 0 ) {
      log.debug("${params.constraints} - attempting json parse");
      def properties = JSON.parse(params.constraints)


      // Scan through all available handlers, see if any match the given properties
      def selected_handler
      def highest_match_so_far = -1;

      def possible_handlers = com.k_int.handlerregistry.Handler.list()

      // For all handlers responding to event base_event
      possible_handlers.each { handler ->
        log.debug("testing ${handler.name}");
        def matching_preconditions = 0;
        handler.preconditions.each { precondition ->
          if ( groovy.util.Eval.me("p",properties,precondition) == true ) {
            matching_preconditions++;
          }
          else {
          }
        }

        log.debug("Handler ${handler.name} matched ${matching_preconditions} out of a possible ${handler.preconditions.size()} preconditions. Current best is ${highest_match_so_far}")

        if ( ( matching_preconditions == handler.preconditions.length ) && ( matching_preconditions > highest_match_so_far ) ) {
          log.debug("Handler is a more specific match than any previous, and is selected")
          selected_handler = handler
          highest_match_so_far = matching_preconditions;
        }
      }

      if ( selected_handler != null ) {
        result.code = 0;
        result.handlerName = selected_handler.name;
      }

    }
    else {
      log.error("No constraints in request");
    }


    render result as JSON
  }
  
}
