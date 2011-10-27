package com.k_int.handlerregistry

import grails.converters.*
import org.apache.shiro.SecurityUtils

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
          log.debug("Evaulate precondition: ${precondition}");

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

          def handler_revisions = HandlerRevision.findAllByOwner(selected_handler, [sort:"revision", order:"desc"]);

          log.debug("Lookup handler revisions returns ${handler_revisions}");
          if ( handler_revisions.size() > 0 ) {
            def the_handler = handler_revisions.get(0);
            result.handler = the_handler.handler;
            result.handler_revision = the_handler.revision;
            result.eventCode = 'com.k_int.aggregator.event.upload.xml';
            result.preconditions = handler.preconditions;
          }
        }
      }

      if ( selected_handler != null ) {
        result.code = 0;
        result.handlerName = selected_handler.name;

        def log_entry = new FindWhenLogEntry(remoteUserId:SecurityUtils.subject.principal,
                                             remoteSystemId:params.remote_instance_id,
                                             remoteConstraints:params.constraints,
                                             selectedHandler:selected_handler,
                                             selectedRevision:result.handler_revision)
        if ( log_entry.save() ) {
          log.debug("Saved log entry");
        }
        else {
          log.debug("Problem saving log entry");
          log_entry.errors.each { err ->
            log.error(err);
          }
        }

        log.debug("Selected handler ${selected_handler.name} for request from remote system with id ${params.remote_instance_id}. Remote user is..${SecurityUtils.subject?.principal}");
      }

    }
    else {
      log.error("No constraints in request");
    }


    render result as JSON
  }
  
}
