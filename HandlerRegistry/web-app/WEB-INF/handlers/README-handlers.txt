

About aggregator handlers
-------------------------

Handlers can use the @grab annotation.. Be warned, this is not currently working for repositories deployed under tomcat, dependencies
must be manually installed!

Handler class names can be anything, however the convention is to end Handler, for example: XCRIHandler

Handlers may obtain their own logger for debug output:

  private static final log = LogFactory.getLog(this)


Handlers must provide the following methods:

  // Return a public name for this handler
  def getHandlerName() {
    "XCRI_CAP"
  }

  // Handler revision
  def getRevision() {
    1
  }

  // The conditions under which this handler will be selected. More specific precondition sets will match ahead of more generic ones
  // This one is an example of a precondition to select the XCRI handler, which is an input XML document who's root element namespace is
  // http://xcri.org/profiles/catalog
  def getPreconditions() {
    [
      'p.rootElementNamespace=="http://xcri.org/profiles/catalog"'
    ]
  }

  // Process the actual document
  def process(props, ctx) {

The props parameter contains a response parameter which must contain the following properties

   props.response.title 
   props.response.resource_identifier

