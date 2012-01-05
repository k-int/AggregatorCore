import com.k_int.aggregator.*

import org.apache.shiro.crypto.hash.Sha256Hash
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ApplicationHolder


class BootStrap {

    def springSecurityService
    def ESWrapperService

    def init = { servletContext ->

      log.debug("System name: ${ApplicationHolder.application.config.aggr.system.name}");

      log.debug("Repository app : Verify default Shiro User");
      def user = ShiroUser.findByUsername("admin")
      if ( user == null ) {
        log.debug("admin user not found.. creating default");
        user = new ShiroUser(username: "admin", passwordHash: new Sha256Hash("password").toHex())
        user.addToPermissions("*:*")
        user.save()
      }
      else {
        log.debug("Repository Admin user verified");
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

      log.debug("Verfy application settings ${ApplicationHolder.application.config.repo?.settings}");
      if ( ApplicationHolder.application.config.repo?.settings != null ) {
        def p = ApplicationHolder.application.config.repo?.settings.toProperties()
        log.debug("Loading local defaults ${p}");
        p.propertyNames().each { pname ->
          verifySetting(pname, p[pname])
        }
      }
      else {
        log.warn("No default settings found in local config....");
      }

      verifySetting('instanceid',java.util.UUID.randomUUID().toString());

      log.debug("Completed veryfying default settings\n\n");
    }

    def destroy = {
    }

    def verifySetting(key,value) {
      log.debug("verify ${key}=${value}");
      def setting = Setting.findByStKey(key)
      if ( setting == null ) {
        setting = new Setting(stKey:key,stValue:value)
        setting.save()
      }
    }
}
