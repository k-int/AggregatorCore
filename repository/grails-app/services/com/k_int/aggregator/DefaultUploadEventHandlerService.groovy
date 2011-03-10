package com.k_int.aggregator

import org.springframework.context.*

class DefaultUploadEventHandlerService implements ApplicationContextAware {

    static transactional = true

    def handlerSelectionService

    def ApplicationContext applicationContext


    @javax.annotation.PostConstruct
    def init() {
      println "Initialising default upload handlers ${this.hashCode()}"
    }

    def handleUnknown(props) {
      println "handleUnknown"
    }

    def handleXML(props) {
      println "handleXML content_type ${props.content_type}"

      // Open the new file so that we can parse the xml
      def xml = new XmlSlurper().parse(new FileInputStream(props.file))

      def root_element_namespace = xml.namespaceURI();
      def root_element_name = xml.name();

      // Root node information....
      println "Root element namespace: ${root_element_namespace} root element: ${root_element_name}"

      // 1. See if there are any handlers capable of dealing with this root element namespace
      def upload_xml_event_map = [ "xmldoc": xml, "rootElementNamespace":root_element_namespace ]
      def schema_handler = handlerSelectionService.selectHandlersFor("com.k_int.aggregator.event.upload.xml",upload_xml_event_map)

      if ( schema_handler == null ) {

        // If the system is configured in dynamic mode, check for the presence of a handler, and set up a default if none can be located.

        def inactive_handlers = EventHandler.findAllByEventCodeAndActive("com.k_int.aggregator.event.upload.xml",false)

        if ( inactive_handlers.size() > 0 ) {
          println "There is an inactive handler that may process this event. Queueing the event until there is a handler capable of processing."
        }
        else {
          println "There are currently no handlers offering to accept documents who's root element is from namespace ${root_element_namespace}"
          println "Checking the k-int core repository....."
          println "No handlers located in core repository.... Creating empty handler, adding deposit"
          def new_handler_name = java.util.UUID.randomUUID().toString();
          String[] preconditions = [ "p.rootElementNamespace==\"${root_element_namespace}\"" ];
          def new_deposit_handler = new ScriptletEventHandler(name:new_handler_name,
                                                              eventCode:'com.k_int.aggregator.event.upload.xml',
                                                              active:false,
                                                              scriptlet:"// An empty scriptlet.\n// Will be passed a params map of name:value pairs, should evaluate a response object.\n\nnull",
                                                              preconditions:preconditions)
          if ( new_deposit_handler.save() ) {
            println "Saved"
          }
          else {
            println new_deposit_handler.errors.allErrors.each {
              println it.defaultMessage
            }

          }

          props.response.code = '2';
          props.response.status = "No handlers available for XML documents using root element namespace ${root_element_namespace}"
          props.response.message = "The document is queued until a handler is available. Deposit event id is ${props.upload_event_token}"

        }

        // Finally, if the system is configured to do so, push the uploaded item onto the pending queue.
      }
      else {
        // Handler found, invoke it,
        if ( schema_handler instanceof ScriptletEventHandler  ) { 
          println "Located handler information - Scriptlet event handler"
        }
        else if ( schema_handler instanceof ServiceEventHandler  ) {
          println "Located handler information - Service event handler : ${schema_handler.targetBeanId}"
          def bean = applicationContext.getBean(schema_handler.targetBeanId)
          println "Calling handler method ${schema_handler.targetMethodName}"
          bean."${schema_handler.targetMethodName}"()
        }

        // println "Delete temp file"
        // temp_file.delete();
      }
    }
}
