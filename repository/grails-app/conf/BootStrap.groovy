import com.k_int.aggregator.*

class BootStrap {

    def springSecurityService

    def init = { servletContext ->

      log.debug("Validating default handler entries....");

      log.debug("defaultDepositHandler");
      def default_deposit_handler = EventHandler.findByName("defaultDepositHandler") ?: new ServiceEventHandler(name:'defaultDepositHandler',
                                                                                                                eventCode:'com.k_int.aggregator.event.upload',
                                                                                                                targetBeanId:'defaultUploadEventHandlerService',
                                                                                                                targetMethodName:'handleUnknown',
                                                                                                                active:true,
                                                                                                                preconditions:[]).save()
      
      log.debug("XMLDepositHandler");
      def xml_deposit_handler = EventHandler.findByName("XMLDepositHandler") ?: new ServiceEventHandler(name:'XMLDepositHandler',
                                                                                                        eventCode:'com.k_int.aggregator.event.upload',
                                                                                                        targetBeanId:'defaultUploadEventHandlerService',
                                                                                                        targetMethodName:'handleXML',
                                                                                                        active:true,
                                                                                                        preconditions:['p.content_type=="application/xml" || p.content_type=="text/xml"']).save()

      log.debug("ECDDepositHandler");
      def ecd_deposit_handler = EventHandler.findByName("ECDHandler") ?: new ServiceEventHandler(name:'ECDHandler',
                                                                                                 eventCode:'com.k_int.aggregator.event.upload.xml',
                                                                                                 targetBeanId:'builtInHandlersService',
                                                                                                 targetMethodName:'handleECD',
                                                                                                 active:true,
                                                                                                 preconditions:['p.rootElementNamespace=="http://dcsf.gov.uk/XMLSchema/Childcare"']).save()

      log.debug("BootStrap::init end");
    }

    def destroy = {
    }
}
