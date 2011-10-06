package com.k_int.aggregator

class RemoteHandlerRepositoryService {

    static transactional = true

    def findHandlerWhen(props) {
      log.debug("Finding any remote handlers for properties : ${props.keySet()}");

    }
}
