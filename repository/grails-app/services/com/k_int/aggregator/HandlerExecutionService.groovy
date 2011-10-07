package com.k_int.aggregator

class HandlerExecutionService {

    static transactional = true

    def process(handler,properties) {
      log.debug("${handler}, ...... Calling eval on ${handler.scriptlet}");
      def p2 = new java.util.HashMap(properties);
      p2['log'] = log
      groovy.util.Eval.x(p2,handler.scriptlet)
      log.debug("Completed call to eval");
    }

}
