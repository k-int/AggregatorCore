@Grab(group='com.gmongo', module='gmongo', version='1.0')

import com.gmongo.GMongo

class ECDHandler {
  
  // handlers have access to the repository mongo service.. suggest you use http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/
  def getHandlerName() {
    "ECDHandler"
  }

  def getRevision() {
    1
  }

  def getPreconditions() {
    [
      'p.rootElementNamespace=="http://dcsf.gov.uk/XMLSchema/Childcare"'
    ]
  }

  def process(properties, ctx) {
    println "This is the ECD handler code......."
  }
}
