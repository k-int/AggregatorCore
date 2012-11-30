import com.k_int.handlerregistry.*
import spring.security.User
import org.springframework.core.io.Resource
import org.codehaus.groovy.grails.commons.ApplicationAttributes 
import grails.util.GrailsUtil

class BootStrap {

  // servletContext.getResourceAsStream("/WEB-INF/myfile.gtpl") 

    def init = { servletContext ->

        def adminUser = User.findByUsername('admin') ?: new User(
               username: 'admin',
               password: 'admin',
               enabled: true).save(failOnError: true)
           
        def anonUser = User.findByUsername('anonymous') ?: new User(
               username: 'anonymous',
               password: 'anonymous',
               enabled: true).save(failOnError: true)
        
      def ctx = servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT) 

      log.debug("Loading handlers from disk cache");

      Resource r = ctx.getResource("/WEB-INF/handlers");
      def f = r.getFile();
      log.debug("got handlers dir: ${f}");

      // see http://groovy.dzone.com/news/class-loading-fun-groovy for info on the strategy being used here

      if ( f.isDirectory() ) {
        GroovyClassLoader gcl = new GroovyClassLoader();
        log.debug("Using class loader: ${gcl.class.name}");

        f.listFiles().each { handler_file ->
          log.debug("Procesing ${handler_file}");

          if ( handler_file.toString().endsWith(".groovy") ) {
            try {
              // Class clazz = gcl.parseClass(handler_file.text);
              log.debug("Compiling ${handler_file}");

              Class clazz = gcl.parseClass(handler_file)
              // log.debug("Number of annotations in ${handler_file} : ${clazz.annotations.length} (This should be >0 for classes with grape annotations)");

              log.debug("Instatiating ${handler_file}");
              Object h = clazz.newInstance();
    
              log.debug("Loaded handler: ${h.getHandlerName()} revision: ${h.getRevision()}, preconditions: ${h.getPreconditions()}");
              def nh = Handler.findByName(h.getHandlerName()) ?: new Handler(name:h.getHandlerName(), preconditions:h.getPreconditions()).save()
              def nr = new HandlerRevision(owner:nh, 
                                           revision:h.getRevision(), 
                                           handlerText:handler_file.text).save();

              log.info("Created handler for ${h.getHandlerName()} - Compliation completed OK");
            }
            catch ( Exception e ) {
              log.error("Unable to compile handler ${handler_file}",e);
            }
          }
          else {
            log.debug("Skipping non-groovy file ${handler_file}");
          }
        }
      }
    }

    def destroy = {
    }
}
