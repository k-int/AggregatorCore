package com.k_int.aggregator

class HandlerExecutionService {

    static transactional = true

    def process(handler,properties) {
      log.debug("${handler}, ...... Calling eval on ${handler.scriptlet}");
      def p2 = new java.util.HashMap(properties);
      // p2['log'] = log
      // groovy.util.Eval.x(p2,handler.scriptlet)

      GroovyClassLoader gcl = new GroovyClassLoader();
      Class clazz = gcl.parseClass(handler.scriptlet);
      Object h = clazz.newInstance();

      log.debug("Calling process method on handler...");
      h.process(log, p2, null);
      log.debug("Completed call to process");
    }

}
