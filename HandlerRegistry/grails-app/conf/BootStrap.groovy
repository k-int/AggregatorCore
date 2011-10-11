import com.k_int.handlerregistry.*

import org.springframework.core.io.Resource
import org.codehaus.groovy.grails.commons.ApplicationAttributes 

class BootStrap {

  // servletContext.getResourceAsStream("/WEB-INF/myfile.gtpl") 

    def init = { servletContext ->

      def ctx = servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT) 



      def h1 = Handler.findByName("ECDHandler") ?: new Handler(name:'ECDHandler',
                                                               preconditions:['p.rootElementNamespace=="http://dcsf.gov.uk/XMLSchema/Childcare"']).save()

      def r1 = new HandlerRevision(owner:h1, revision:1, handler:"x.log.debug(\"ECDHandler v1.0\")\nx.log.debug(\"Line2\");").save();

     
     
      def h2 = Handler.findByName("XCRI_CAP") ?: new Handler(name:'XCRI_CAP',
                                                               preconditions:['p.rootElementNamespace=="http://xcri.org/profiles/catalog"']).save()
      def r2 = new HandlerRevision(owner:h2, revision:1, handler:"x.log.debug(\"XCRI_CAP v1.0\")\nx.log.debug(\"Line2\");").save();


      Resource r = ctx.getResource("/handlers");
      def f = r.getFile();
      log.debug("got handlers dir: ${f}");
    }

    def destroy = {
    }
}
