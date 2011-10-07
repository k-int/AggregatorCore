package com.k_int.aggregator

import org.springframework.context.*

class HandlerSelectionService implements ApplicationContextAware {

    static scope = "singleton"
    static transactional = true

    def ApplicationContext applicationContext
    def remoteHandlerRepositoryService
    def handlerExecutionService

    @javax.annotation.PostConstruct
    def init() {
      log.debug("Initialising handler selection service ${this.hashCode()}")
    }

    def selectHandlersFor(base_event, properties) {
      
      log.debug("Locating handlers for ${base_event}...")

      // Load the events
      def possible_handlers = EventHandler.findAllByEventCodeAndActive(base_event,true)
      log.debug("Located the following possible event handlers: ${possible_handlers}")

      def selected_handler
      def highest_match_so_far = -1;

      // For all handlers responding to event base_event
      possible_handlers.each { handler ->
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
        log.debug("After evaluation, selected handler for this event is ${selected_handler.name}")
      }
      else {
        selected_handler = remoteHandlerRepositoryService.findHandlerWhen(properties);
      }

      selected_handler
    }

    def executeHandler(handler, props) {
       log.debug("Request to execute handler ${handler.name}....")

      if ( handler instanceof ServiceEventHandler ) {
        log.debug("handler is a ServiceEventHandler... get hold of the bean from spring context")
        def bean = applicationContext.getBean(handler.targetBeanId)

        if ( bean != null ) {
          log.debug("Got hold of ${bean}... Calling ${handler.targetMethodName}")
          bean."${handler.targetMethodName}"(props)
          log.debug("Call complete")
        }
        else {
          log.error("Unable to identify bean with name ${handler.targetBeanId}")
        }
      }
      else {
        log.warn("Non service based handlers not implemented yet")
      }
    }
}
