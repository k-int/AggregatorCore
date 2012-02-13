package com.k_int.aggregator

import org.springframework.context.support .*;

class TerminologyServiceMessageSource extends AbstractMessageSource {

  protected MessageFormat resolveCode(String code, Locale locale) { 
    // Message msg = Message.findByCodeAndLocale(code, locale)
    // def format
    // if(msg) {
    //   format = new MessageFormat(msg.text, msg.locale)
    // }
    // else {
    //   format = new MessageFormat(code, locale )   
    // }
    // return format;
    null;
  }
}
