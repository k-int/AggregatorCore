package com.k_int.aggregator

/**
 *  A ServiceEventHandler responds to the identified event by calling the specified method on the specified bean id
 */
class ServiceEventHandler extends EventHandler {

    String targetBeanId
    String targetMethodName

    static constraints = {
    }
}
