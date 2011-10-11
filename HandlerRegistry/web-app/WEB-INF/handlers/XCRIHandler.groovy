@Grab(group='com.gmongo', module='gmongo', version='0.5.1')

import com.gmongo.GMongo

class XCRIHandler {

  // handlers have access to the repository mongo service.. suggest you use http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/
  def getHandlerName() {
    "XCRI_CAP"
  }

  def getRevision() {
    1
  }

  def getPreconditions() {
    [
      'p.rootElementNamespace=="http://xcri.org/profiles/catalog"'
    ]
  }

  def process(log, props, ctx) {
    log.debug("this is a doodah");
  }
}
