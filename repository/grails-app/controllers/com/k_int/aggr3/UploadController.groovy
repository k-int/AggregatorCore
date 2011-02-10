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

      // Set up the propeties for the upload event, in this case event=com.k_int.aggregator.event.upload and mimetype=<mimetype>
      // We are looking for any handlers willing to accept this event given the appropriate properties
      def event_properties = [:]

      // Firstly we need to select an appropriate handler for the com.k_int.aggregator.event.upload event
      if ( handlerSelectionService ) {
        def handlers_to_invoke = handlerSelectionService.selectHandlersFor("com.k_int.aggregator.event.upload",event_properties)
      }
      else {
        response.code = '-1';
        response.status = 'Internal Error';
        response.message = 'The handler selection service is not configured. Please contact the support desk';
      }

      render(view:"index",model:response)
    }
}
