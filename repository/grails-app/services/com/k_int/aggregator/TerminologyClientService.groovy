package com.k_int.aggregator

import org.springframework.context.*
import com.gmongo.GMongo

/**
 * Some useful links: http://graemerocher.blogspot.com/2010/04/reading-i18n-messages-from-database.html and http://efreedom.com/Question/1-8100312/Grails-I18n-Database-Default-Back-File
 */

class TerminologyClientService implements ApplicationContextAware {

  def ApplicationContext applicationContext

  def mongo = new com.gmongo.GMongo();
  def termserv_db = null;


  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
    log.debug("Init completed");
    termserv_db = mongo.getDB("kitermserv")
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
    log.debug("Destroy completed");
  }


  // See if a term exists
  // lens is our local rewrite rules for this public vocabulary
  //
  def checkTermExists(identifier, context="unspecified", lens = null, remember = false) {
    log.debug("Check term exists ${context}, ${identifier}");

    def located_term = termserv_db.terms.findOne(context: context, identifier: identifier, lens: lens)

    if ( ( located_term == null ) && ( lens != null ) ) {
    }
    else {
    }

    if ( located_term == null ) {
      if ( remember == true ) {
        log.debug("attempt to lookup term ${identifier} via lens ${lens} failed. Remember==true, so creating new mapping");
      }
      return true
    }

    false
  }

  def registerTerm(term, context="unspecified") {
    log.debug("Register term ${context}, ${term}");
    false
  }

  def lookupTerm(identifier, context="unspecified", lens = null) {
    log.debug("Lookup Term ${context}, ${identifier}");
    false
  }

  def lookupContext(context) {
    def located_context = termserv_db.contextHeader.findOne(id: context)
    if ( located_context == null ) {
      log.debug("Context ${context} not located, creating....");
      located_context = [:]
      located_context.identifier = context;
      termserv_db.contextHeader.save(located_context);
    }

    located_context
  }
}
