import com.k_int.aggregator.*
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.converters.JSON
import spring.security.User

class BootStrap {

    def springSecurityService
    def ESWrapperService
    def terminologyClientService

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

        // Controlled vocabs
        terminologyClientService.checkVocabExists('subject', 'Subjects');
        terminologyClientService.checkVocabExists('qualification', 'Qualifications');
        terminologyClientService.checkVocabExists('level', 'Levels');
        terminologyClientService.checkVocabExists('scheme', 'Schemes');
        terminologyClientService.checkVocabExists('language', 'Language');
        terminologyClientService.checkVocabExists('studyMode', 'Study Mode');
        terminologyClientService.checkVocabExists('attendanceMode', 'Attendance Mode');
        terminologyClientService.checkVocabExists('attendancePattern', 'Attendance Pattern');
        terminologyClientService.checkVocabExists('language', 'Language');

        log.debug("Completed veryfying default settings\n\n");

        //register JSON converter for CanonicalIdentifier
        JSON.registerObjectMarshaller(CanonicalIdentifier) 
        {
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['canonicalIdentifier'] = it.canonicalIdentifier
            returnArray['owner'] = it.owner
            return returnArray 
        }
    
    }
    
    def adminUser = User.findByUsername('admin') ?: new User(
        username: 'admin',
        password: 'password',
        enabled: true).save(failOnError: true)

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
