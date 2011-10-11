import com.k_int.handlerregistry.*

import org.springframework.core.io.Resource
import org.codehaus.groovy.grails.commons.ApplicationAttributes 

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
        f.listFiles().each { handler_file ->
          log.debug("Procesing ${handler_file}");

          GroovyClassLoader gcl = new GroovyClassLoader();
          Class clazz = gcl.parseClass(handler_file.text);
          Object h = clazz.newInstance();

          log.debug("Loading handler: ${h.getHandlerName()} revision: ${h.getRevision()}, preconditions: ${h.getPreconditions()}");
          def nh = Handler.findByName(h.getHandlerName()) ?: new Handler(name:h.getHandlerName(), preconditions:h.getPreconditions()).save()
          def nr = new HandlerRevision(owner:nh, 
                                       revision:h.getRevision(), 
                                       handler:handler_file.text).save();
        }
      }
    }

    def destroy = {
    }
}
