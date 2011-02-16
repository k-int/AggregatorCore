package com.k_int.aggr3

import com.k_int.aggregator.*;
import org.apache.shiro.SecurityUtils

class UploadController {

    def handlerSelectionService

    def index = { 

      def user = User.get(SecurityUtils.getSubject()?.getPrincipal()) 

      println "Index.... User: ${SecurityUtils.getSubject()?.getPrincipal()} -- ${user}"

      // Empty response object
      def response = ["code":"0"]
      response
    }

    def validateUploadDir(path) {
      File f = new File(path);
      if ( ! f.exists() ) {
        println "Creating upload directory path"
        f.mkdirs();
      }
    }

    def save = { 
      println "Save.... User: ${SecurityUtils.getSubject()?.getPrincipal()}"

      // This is a secured resource... get user details
      def user = User.get(SecurityUtils.getSubject()?.getPrincipal()) 

      def provider = params.owner;
      def response = ["code": 0]
      def file = request.getFile("upload")

      println "Validating provider : ${provider}"

      // If none present, does the user have a default?
      if ( ( provider == null ) || ( provider == '' ) ) {
        provider = user?.defaultProvider?.code
      }

      // Validate the presence of a data provider
      if ( provider == null ) {
        println "Aborting save, no provider present"
        render(view:"index",model:response)
        return
      }
      
      // If provider present, but doesn't exist, does user have permission to dynamically create?
      def provider_object = DataProvider.findByCode(provider)

      if ( provider_object == null ) {
        println "Unable to locate provider with code ${provider}"
      }
      
      // Validate user permission to deposit on behalf of that provider
      if ( org.apache.shiro.SecurityUtils.subject.isPermitted('resource:deposit:222') ) {
        println "User has upload permission for provider \"${provider}\""
      }
      else {
        println "No user permission to upload for \"${provider}\""
      }

      if ( file != null ) {
        def content_type = file.contentType

        validateUploadDir("./filestore");

        // Store the uploaded file for future reference.

        // bytes byte[] = file.getBytes()
        println "Storring uploaded file in temporary storage...."
        def deposit_token = java.util.UUID.randomUUID().toString();
        def temp_file_name = "./filestore/${deposit_token}.xml";
        def temp_file = new File(temp_file_name);

        // Copy the upload file to a temporary space
        file.transferTo(temp_file);

        println "Create deposit event ${deposit_token}"
        DepositEvent de = new DepositEvent(depositToken:deposit_token, status:'1',uploadUser:user)
        if ( de.save() ) {
          println "Created..."

          // Set up the propeties for the upload event, in this case event=com.k_int.aggregator.event.upload and mimetype=<mimetype>
          // We are looking for any handlers willing to accept this event given the appropriate properties
          def event_properties = ["content_type":content_type, "file":temp_file, "response":response, "upload_event_token":deposit_token, "user":user]

          // Firstly we need to select an appropriate handler for the com.k_int.aggregator.event.upload event
          if ( handlerSelectionService ) {
            def handler_to_invoke = handlerSelectionService.selectHandlersFor("com.k_int.aggregator.event.upload",event_properties)
            if ( handler_to_invoke != null ) {
              handlerSelectionService.executeHandler(handler_to_invoke,event_properties)
              // response.code should be 0 == Processed, 1==In process, 2==Queued or Some other Error
            }
            else {
              response.code = '2';
              response.status = 'No handler available. Deposit is queued pending system configuration';
              response.message = 'Unable to locate handler';
            }
          }
          else {
            response.code = '-2';
            response.status = 'Internal Error';
            response.message = 'The handler selection service is not configured. This deposit has not been recorded, please contact the support and re-submit';
          }

          // update the status in the deposit event to whatever response.code is.
          de.status = response.code;
          de.save();
        }
        else {
          response.code = '-2';
          response.status = 'Internal Error';
          response.message = 'Unable to create deposit event. This deposit has not been recorded, please contact support and re-submit.';
          println de.errors.allErrors.each {
            println it.defaultMessage
          }
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
