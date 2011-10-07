package com.k_int.aggregator

import org.springframework.context.*

class DefaultUploadEventHandlerService implements ApplicationContextAware {

    static transactional = true

    def handlerSelectionService
    def handlerExecutionService

    def ApplicationContext applicationContext


    @javax.annotation.PostConstruct
    def init() {
      log.debug("Initialising default upload handlers ${this.hashCode()}")
    }

    def handleUnknown(props) {
      log.debug("handleUnknown")
    }

    def handleXML(props) {
      log.debug("handleXML content_type ${props.content_type}")

      // Open the new file so that we can parse the xml
      def xml = new XmlSlurper().parse(new FileInputStream(props.file))

      // def create_blank_handler_policy = false;

      def root_element_namespace = xml.namespaceURI();
      def root_element_name = xml.name();

      // Root node information....
      log.debug( "Root element namespace: ${root_element_namespace} root element: ${root_element_name}")

      // 1. See if there are any handlers capable of dealing with this root element namespace
      // def upload_xml_event_map = [ "xmldoc": xml, 
      def upload_xml_event_map = [ "rootElementNamespace":root_element_namespace,
                                   "rootElement":root_element_name ]

      def schema_handler = handlerSelectionService.selectHandlersFor("com.k_int.aggregator.event.upload.xml",upload_xml_event_map)

      if ( schema_handler == null ) {

        // If the system is configured in dynamic mode, check for the presence of a handler, and set up a default if none can be located.

        def inactive_handlers = EventHandler.findAllByEventCodeAndActive("com.k_int.aggregator.event.upload.xml",false)

        if ( inactive_handlers.size() > 0 ) {
          log.debug( "There is an inactive handler that may process this event. Queueing the event until there is a handler capable of processing.")
        }
        else {
          log.debug( "There are currently no handlers offering to accept documents who's root element is from namespace ${root_element_namespace}")

          // String[] preconditions = [ "p.rootElementNamespace==\"${root_element_namespace}\"" ];
          // log.debug( "Checking the k-int core repository..... properties: ${upload_xml_event_map.keySet()}")

          // def remote_handler = checkRemoteRepository(upload_xml_event_map);

          // Check the core repository
          // if ( remote_handler == null ) {

          //   if ( create_blank_handler_policy ) {
          //     log.debug( "No handlers located in core repository.... Creating empty handler, adding deposit")
          //     def new_handler_name = java.util.UUID.randomUUID().toString();


          //     def new_deposit_handler = new ScriptletEventHandler(name:new_handler_name,
          //                                                         eventCode:'com.k_int.aggregator.event.upload.xml',
          //                                                         active:false,
          //                                                         scriptlet:"// An empty scriptlet.\n// Will be passed a params map of name:value pairs, should evaluate a response object.\n\nnull",
          //                                                         preconditions:preconditions)
          //     if ( new_deposit_handler.save() ) {
          //       log.debug("Saved new event handler for root element namespace: ${root_element_namespace}...")
          //     }
          //     else {
          //       new_deposit_handler.errors.allErrors.each {
          //         log.error(it.defaultMessage)
          //       }
          //     }
          //   }
          //   else {
          //     log.debug("No handler available. System policy is not to create blank handlers. Document queued");
          //   }

            props.response.code = '2';
            props.response.status = "No handlers available for XML documents using root element namespace ${root_element_namespace}"
            props.response.message = "The document is queued until a handler is available. Deposit event id is ${props.upload_event_token}"
          // }
          // else {
          //   log.debug("Remote handler located... importing...");
          // }
        }

        // Finally, if the system is configured to do so, push the uploaded item onto the pending queue.
      }
      else {
        // Handler found, invoke it,
        if ( schema_handler instanceof ScriptletEventHandler  ) { 
          log.debug("Located handler information - Scriptlet event handler")
          def xml_params = new java.util.HashMap(props);
          xml_params["xml"] = xml;
          xml_params["rootElementNamespace"] = root_element_namespace
          xml_params["rootElement"] = root_element_name
          handlerExecutionService.process(schema_handler, xml_params);
        }
        else if ( schema_handler instanceof ServiceEventHandler  ) {
          log.debug( "Located handler information - Service event handler : ${schema_handler.targetBeanId}")
          def bean = applicationContext.getBean(schema_handler.targetBeanId)
          log.debug( "Calling handler method ${schema_handler.targetMethodName}")

          // Clone the props and add anything else we may need
          def xml_params = new java.util.HashMap(props);
          xml_params["xml"] = xml;
          xml_params["rootElementNamespace"] = root_element_namespace
          xml_params["rootElement"] = root_element_name

          bean."${schema_handler.targetMethodName}"(xml_params)
        }

        // println "Delete temp file"
        // temp_file.delete();
      }
    }
}
