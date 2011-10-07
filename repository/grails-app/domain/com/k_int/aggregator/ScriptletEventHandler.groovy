package com.k_int.aggregator

class ScriptletEventHandler extends EventHandler {

  String scriptlet

    static constraints = {
      scriptlet(maxSize:1000000)
    }
}
