package com.k_int.aggregator

import org.springframework.context.* 

class HandlerExecutionService implements ApplicationContextAware {

    def handler_cache = [:] 

    ApplicationContext applicationContext 

    static transactional = true

    def process(handler,properties) {

      def p2 = new java.util.HashMap(properties);

      def hi = getHandlerInstance(handler);

      if ( hi != null ) {
        log.debug("Calling process method on handler...");
        hi.process(p2, applicationContext);
        log.debug("Completed call to process");
      }
      else {
        log.error("Unable to locate handler instance for ${handler}");
      }
    }


    def getHandlerInstance(handler_definition) {
      def hi = handler_cache[handler_definition.id]

      if ( hi == null ) {
        // GroovyClassLoader gcl = new GroovyClassLoader();
        // Class clazz = gcl.parseClass(handler.scriptlet);
        Class clazz = new GroovyClassLoader(this.class.getClassLoader()).parseClass(handler_definition.scriptlet);
        hi = clazz.newInstance();
        log.debug("Cache new instance of handler with id ${handler_definition.id}");
        handler_cache[handler_definition.id] = hi;
      }

      hi
    }
}
