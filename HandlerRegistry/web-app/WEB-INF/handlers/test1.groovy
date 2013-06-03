@Grab(group='com.gmongo', module='gmongo', version='1.2')

import com.gmongo.GMongo

class test1 {
  
    // handlers have access to the repository mongo service.. suggest you use http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/
    def getHandlerName() {
    "HelloWorld"
    }

    def getRevision() {
        1
    }

    def getPreconditions() {
        [
      "1==2"
        ]
    }

    def process() {
        println "This is the code......."
    }
}
