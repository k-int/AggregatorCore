import com.k_int.aggregator.*

import org.apache.shiro.crypto.hash.Sha256Hash

class BootStrap {

    def springSecurityService
    def ESWrapperService

    def init = { servletContext ->

      log.debug("Verify default Shiro User");
      def user = ShiroUser.findByUsername("admin")
      if ( user == null ) {
        log.debug("admin user not found.. creating default");
        user = new ShiroUser(username: "admin", passwordHash: new Sha256Hash("password").toHex())
        user.addToPermissions("*:*")
        user.save()
      }
      else {
        log.debug("Admin user verified");
      }

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

      // log.debug("ECDDepositHandler");
      // def ecd_deposit_handler = EventHandler.findByName("ECDHandler") ?: new ServiceEventHandler(name:'ECDHandler',
      //                                                                                            eventCode:'com.k_int.aggregator.event.upload.xml',
      //                                                                                            targetBeanId:'builtInHandlersService',
      //                                                                                            targetMethodName:'handleECD',
      //                                                                                            active:true,
      //                                                                                            preconditions:['p.rootElementNamespace=="http://dcsf.gov.uk/XMLSchema/Childcare"']).save()

      def system_id = Setting.findByStKey('instanceid')
      if ( system_id == null ) {
        system_id = new Setting(stKey:'systemid',stValue:java.util.UUID.randomUUID().toString())
        system_id.save()
      }
      log.debug("BootStrap::init end. syste, instanceid is ${system_id.stValue}");
    }

    def destroy = {
    }
}
