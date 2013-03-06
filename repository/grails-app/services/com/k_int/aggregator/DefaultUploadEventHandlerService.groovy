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
        props.response.eventLog.add([ts:System.currentTimeMillis(),lvl:'info',type:'msg',msg:"Content type detected as XML : ${props.content_type}"])

        try {
  
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
                    props.response.code = '2';
                    props.response.status = "No handlers available for XML documents using root element namespace ${root_element_namespace}"
                    props.response.message = "The document is queued until a handler is available. Deposit event id is ${props.upload_event_token}"
                }
  
                // Finally, if the system is configured to do so, push the uploaded item onto the pending queue.
            }
            else {
                props.response.eventLog.add([ts:System.currentTimeMillis(), lvl:'info', type:'msg',msg:"Located event handler for schema: ${schema_handler.name}"])
  
                // Handler found, invoke it,
                if ( schema_handler instanceof ScriptletEventHandler  ) { 
                    log.debug("Located handler information - Scriptlet event handler - ${schema_handler.name}")
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
        catch ( org.xml.sax.SAXParseException spe ) {
            props.response.eventLog.add([ts:System.currentTimeMillis(), lvl:'error', type:'msg',msg:"XML Parse Failure: ${spe.message}"])
            props.response.code = '4';
            props.response.status = "XML Parse Failure";
            props.response.message = spe.message
        }
        catch ( Exception e ) {
            props.response.eventLog.add([ts:System.currentTimeMillis(), lvl:'error', type:'msg',msg:"Unhandled Internal Error: ${e.message}"])
            props.response.code = '4';
            props.response.status = "Unhandled Internal Error";
            props.response.message = e.message
        }
    }

}
