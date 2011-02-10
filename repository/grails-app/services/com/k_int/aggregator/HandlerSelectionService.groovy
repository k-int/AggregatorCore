package com.k_int.aggregator

import org.springframework.context.*

class HandlerSelectionService implements ApplicationContextAware {

    static scope = "singleton"
    static transactional = true

    def ApplicationContext applicationContext

    @javax.annotation.PostConstruct
    def init() {
      println "Initialising handler selection service ${this.hashCode()}"
    }

    def selectHandlersFor(base_event, properties) {
      
      println "Locating handlers for ${base_event}, ${properties}"

      // Load the events
      def possible_handlers = EventHandler.findAllByEventCode(base_event)
      println "Located the following possible event handlers: ${possible_handlers}"

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

        println "Handler ${handler.name} matched ${matching_preconditions} out of a possible ${handler.preconditions.size()} preconditions. Current best is ${highest_match_so_far}"

        if ( ( matching_preconditions == handler.preconditions.length ) && ( matching_preconditions > highest_match_so_far ) ) { 
          println "Handler is a more specific match than any previous, and is selected"
          selected_handler = handler 
          highest_match_so_far = matching_preconditions;
        }
      }

      if ( selected_handler != null ) {
        println "After evaluation, selected handler for this event is ${selected_handler.name}"
      }

      selected_handler
    }

    def executeHandler(handler, props) {
       println "Request to execute handler ${handler.name}...."

      if ( handler instanceof ServiceEventHandler ) {
        println "ServiceEventHandler..."
        def bean = applicationContext.getBean(handler.targetBeanId)
        println "Got hold of ${bean}"
      }
      else {
        println "Not implemented yet"
      }
    }
}
