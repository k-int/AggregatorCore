import com.k_int.handlerregistry.*

class BootStrap {

    def init = { servletContext ->


      def h1 = Handler.findByName("ECDHandler") ?: new Handler(name:'ECDHandler',
                                                               preconditions:['p.rootElementNamespace=="http://dcsf.gov.uk/XMLSchema/Childcare"']).save()

      def r1 = new HandlerRevision(owner:h1, revision:1, handler:"log.debug(\"ECDHandler v1.0\")\nlog.debug(\"Line2\");").save();

    }
    def destroy = {
    }
}
