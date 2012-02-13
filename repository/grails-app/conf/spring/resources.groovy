// Place your Spring DSL code here
beans = {

  // Attempting to use the terminology for some of our messages..
  // We create our new message source refencing the old file based source.
  messageSource(com.k_int.aggregator.TerminologyServiceMessageSource) {
    messageBundleMessageSource = ref("messageBundleMessageSource")
  }    
  messageBundleMessageSource(org.codehaus.groovy.grails.context.support.PluginAwareResourceBundleMessageSource) {
    basenames = "WEB-INF/grails-app/i18n/messages"
  }
}
