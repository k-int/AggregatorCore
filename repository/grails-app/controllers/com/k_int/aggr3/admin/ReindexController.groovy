package com.k_int.aggr3.admin

import com.k_int.aggregator.*

class ReindexController {

  def remoteHandlerRepositoryService
  def reindexService

  def index() { 

    log.debug("Reindex controller");

    def result = [:]

    result.sysid = remoteHandlerRepositoryService.sys_id

    reindexService.attemptReindex()

    result
  }
}
