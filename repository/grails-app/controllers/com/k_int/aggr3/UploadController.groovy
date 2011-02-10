package com.k_int.aggr3

class UploadController {

    def handlerSelectionService

    def index = { 
      println "Index...."
      // Empty response object
      def response = ["code":"0"]
      response
    }

    def save = { 
      println "Save...."
      def response = ["code": 0]

      def file = request.getFile("upload")
      if ( file != null ) {
        def content_type = file.contentType

        // Set up the propeties for the upload event, in this case event=com.k_int.aggregator.event.upload and mimetype=<mimetype>
        // We are looking for any handlers willing to accept this event given the appropriate properties
        def event_properties = ["content_type":content_type]

        // Firstly we need to select an appropriate handler for the com.k_int.aggregator.event.upload event
        if ( handlerSelectionService ) {
          def handler_to_invoke = handlerSelectionService.selectHandlersFor("com.k_int.aggregator.event.upload",event_properties)
          if ( handler_to_invoke != null ) {
            handlerSelectionService.executeHandler(handler_to_invoke,event_properties);
          }
          else {
            response.code = '-3';
            response.status = 'Internal Error';
            response.message = 'Unable to locate handler';
          }
        }
        else {
          response.code = '-2';
          response.status = 'Internal Error';
          response.message = 'The handler selection service is not configured. Please contact the support desk';
        }

      }
      else {
        response.code = '-1';
        response.status = 'Validation Error - No file uploaded';
        response.message = 'You must supply an upload parameter in the form of a multipart-file field';
      }

      println "Response: ${response}"

      render(view:"index",model:response)
    }
}
