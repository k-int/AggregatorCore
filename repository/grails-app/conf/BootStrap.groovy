import com.k_int.aggregator.*

import org.apache.shiro.crypto.hash.Sha256Hash
import grails.util.GrailsUtil

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

      switch (GrailsUtil.environment) {
        case 'development':
          log.debug("Configuring for development environment");
          verifySetting('handlerServiceURL','http://localhost:8090/HandlerRegistry');
          break
        case 'production':
          log.debug("Configuring for production environment");
          verifySetting('handlerServiceURL','http://aggrconf.k-int.com');
          break
      }
      verifySetting('instanceid',java.util.UUID.randomUUID().toString());
      verifySetting('handlerServiceUser','anonymous');
      verifySetting('handlerServicePass','anonymous');
    }

    def destroy = {
    }

    def verifySetting(key,value) {
      def setting = Setting.findByStKey(key)
      if ( setting == null ) {
        setting = new Setting(stKey:key,stValue:value)
        setting.save()
      }
    }
}
