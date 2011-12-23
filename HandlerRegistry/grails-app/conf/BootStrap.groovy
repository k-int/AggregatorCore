import com.k_int.handlerregistry.*

import org.springframework.core.io.Resource
import org.codehaus.groovy.grails.commons.ApplicationAttributes 
import org.apache.shiro.crypto.hash.Sha256Hash
import grails.util.GrailsUtil

class BootStrap {

  // servletContext.getResourceAsStream("/WEB-INF/myfile.gtpl") 

    def init = { servletContext ->

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

          // Class clazz = gcl.parseClass(handler_file.text);
          Class clazz = gcl.parseClass(handler_file)
          // log.debug("Number of annotations in ${handler_file} : ${clazz.annotations.length} (This should be >0 for classes with grape annotations)");
          Object h = clazz.newInstance();

          log.debug("Loading handler: ${h.getHandlerName()} revision: ${h.getRevision()}, preconditions: ${h.getPreconditions()}");
          def nh = Handler.findByName(h.getHandlerName()) ?: new Handler(name:h.getHandlerName(), preconditions:h.getPreconditions()).save()
          def nr = new HandlerRevision(owner:nh, 
                                       revision:h.getRevision(), 
                                       handlerText:handler_file.text).save();
        }
      }

      log.debug("Verify default anonymous User");
      def anonymous_user = ShiroUser.findByUsername("anonymous")
      if ( anonymous_user == null ) {
        log.debug("anonymous user not found.. creating");
        anonymous_user = new ShiroUser(username: "anonymous", passwordHash: new Sha256Hash("anonymous").toHex())
        anonymous_user.addToPermissions("*:*")
        anonymous_user.save()
      }
      else {
        log.debug("anonymous user verified");
      }

      log.debug("Verify default admin User");
      def admin_user = ShiroUser.findByUsername("admin")
      if ( admin_user == null ) {
        log.debug("anonymous user not found.. creating");
        admin_user = new ShiroUser(username: "admin", passwordHash: new Sha256Hash("admin").toHex())
        admin_user.addToPermissions("*:*")
        admin_user.save()
      }
      else {
        log.debug("admin user verified");
      }


    }

    def destroy = {
    }
}
