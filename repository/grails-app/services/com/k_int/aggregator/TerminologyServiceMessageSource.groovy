package com.k_int.aggregator

import org.springframework.context.support .*;
import java.text.MessageFormat;

// Largely based on code from http://efreedom.com/Question/1-8100312/Grails-I18n-Database-Default-Back-File

class TerminologyServiceMessageSource extends AbstractMessageSource {

    def messageBundleMessageSource

    protected MessageFormat resolveCode(String code, Locale locale) { 
    
        // Message msg = Message.findByCodeAndLocale(code, locale)
        def format
        // if(msg) {
        //   format = new MessageFormat(msg.text, msg.locale)
        // }
        // else {
        format = messageBundleMessageSource.resolveCode(code, locale)
        // }
        return format;

        // Message msg = Message.findByCodeAndLocale(code, locale)
        // def format
        // if(msg) {
        //   format = new MessageFormat(msg.text, msg.locale)
        // }
        // else {
        //   format = new MessageFormat(code, locale )   
        // }
        // return format;
    }
}
