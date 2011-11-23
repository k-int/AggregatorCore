package com.k_int.aggr3

import com.k_int.aggregator.*;
import org.apache.shiro.SecurityUtils

class UploadController {

  def grailsApplication
  def handlerSelectionService

  def nimbleService
  def userService
  def adminsService

  def index = { 

    // def user = User.get(SecurityUtils.getSubject()?.getPrincipal()) 

    log.debug("Index....")

    // Empty response object
    def response = ["code":"0"]
    response
  }

  def validateUploadDir(path) {
    File f = new File(path);
    if ( ! f.exists() ) {
      log.debug("Creating upload directory path")
      f.mkdirs();
    }
  }

  def save = { 
    log.debug("Save");
    // println "Save.... User: ${SecurityUtils.getSubject()?.getPrincipal()}"

    // This is a secured resource... get user details
    // def user = User.get(SecurityUtils.getSubject()?.getPrincipal()) 
    // def effective_user = user;

    def provider = params.owner;
    def on_behalf_of = params.on_behalf_of;
    def response = ["code": 0, eventLog:[]]
    def file = request.getFile("upload")

    log.debug( "Validating provider : ${provider}")

    // If none present, does the user have a default?
    // if ( ( provider == null ) || ( provider == '' ) ) {
    //   provider = user?.defaultProvider?.code
    // }

    // Validate the presence of a data provider
    if ( ( provider == null ) || ( provider == '' ) ) {
      log.debug("Aborting save, no provider present")
      response.code = '-3';
      response.status = 'No data provider';
      response.message = 'No data provider';
      render(view:"index",model:response)
      return
    }
    else {
      log.debug( "Using provider code ${provider}")
    }
    
    // Try and look up the provider
    def provider_object = DataProvider.findByCode(provider)
    
    // If provider present in request, but doesn't exist in db, does user have permission to dynamically create?
    if ( provider_object == null ) {
      log.debug("Unable to locate provider with code ${provider}")
      if ( 1==1 ) {
        println "User has create provider permission.. Creating ${provider} provider"
        provider_object = new DataProvider(code:provider)
        if ( provider_object.save() ) {
          log.debug("New provider ${provider} created for user...");
        }
        else {
          log.error("Problems creating provider ${provider}");
        }
      }
      else {
        response.code = '-4';
        response.status = 'Error'
        response.message = 'An unknown provider was specified, and the logged in user does not have create provider permission';
        render(view:"index",model:response)
        return
      }
    }

    // Is the upload an administrator on behalf of a particular user. If so, validate
    if ( on_behalf_of != null ) {
      // if ( org.apache.shiro.SecurityUtils.subject.hasRole(AdminsService.ADMIN_ROLE) ) {
      //   println "Request for on_behalf_of and user has admin perms..."
      //   effective_user = User.findByUsername(on_behalf_of)
      //   if ( ( effective_user == null ) && ( params.create_user == 'Y' ) ) {
      //     // The effective user doesn't exist. If the request contains create_user=Y then we will create one
      //     println "Creating missing user account for ${on_behalf_of}"
      //     createUser(on_behalf_of,'**changeme**',params.user_full_name)
      //   }
      // }
      // else {
      //   println "Request contained on_behalf_of parameter, but authenticated user has no administrative permission"
      //   response.code = '-4';
      //   response.status = 'error'
      //   response.message = 'on_behalf_of not available for this user'
      //   render(view:"index",model:response)
      //   return
      // }
    }
    
    // Validate user permission to deposit on behalf of that provider
    // if ( org.apache.shiro.SecurityUtils.subject.isPermitted('resource:deposit:222') ) {
    //   println "User has upload permission for provider \"${provider}\""
    // }
    // else {
    //   println "No user permission to upload for \"${provider}\""
    // }

    if ( file != null ) {
      def content_type = file.contentType
      def temp_file_name 
      def context_dir

      validateUploadDir("./filestore");

      // Store the uploaded file for future reference.

      // bytes byte[] = file.getBytes()
      log.debug( "Storring uploaded file in temporary storage.... (content_type=${content_type})")
      def deposit_token = java.util.UUID.randomUUID().toString();

      // Here we need to take special action if a compressed archive has been uploaded
      if ( content_type == 'application/zip' ) {
        log.debug("uploaded item is a zip archive")
        temp_file_name = "./filestore/${deposit_token}.zip";
      }
      else {
        temp_file_name = "./filestore/${deposit_token}.xml";
      }

      def temp_file = new File(temp_file_name);

      // Copy the upload file to a temporary space
      file.transferTo(temp_file);

      // If it's a compressed archive, we need to unpack it here too
      if ( content_type == 'application/zip' ) {
        log.debug("unpack compressed archive");
        context_dir = "./filestore/${deposit_token}"
        validateUploadDir(context_dir)
        extract(temp_file, context_dir)

        // After extracting, we look for a manifest file. If the request specified a manifest property, use that. Otherwise, look for manifest.xml
        def manifest_filename = params.manifest ?: "manifest.xml";

        def manifest_file = new File("${context_dir}/${manifest_filename}")
        if ( manifest_file.exists() ) {
          log.debug("Manifest file exists! ${manifest_filename}");
        }
        else {
          log.debug("Can't locate manifest file... ${manifest_filename}");
        }
      }

      log.debug( "Create deposit event ${deposit_token}")
      // DepositEvent de = new DepositEvent(depositToken:deposit_token, status:'1',uploadUser:user)
      DepositEvent de = new DepositEvent(depositToken:deposit_token, 
                                         status:'1')
      if ( de.save() ) {
        log.debug( "Created...")

        // Set up the propeties for the upload event, in this case event=com.k_int.aggregator.event.upload and mimetype=<mimetype>
        // We are looking for any handlers willing to accept this event given the appropriate properties
        // def event_properties = ["content_type":content_type, "file":temp_file, "response":response, "upload_event_token":deposit_token, "user":user]
        def event_properties = ["owner":provider,
                                "content_type":content_type, 
                                "file":temp_file, 
                                "response":response, 
                                "upload_event_token":deposit_token]

        // Firstly we need to select an appropriate handler for the com.k_int.aggregator.event.upload event
        if ( handlerSelectionService ) {
          def handler_to_invoke = handlerSelectionService.selectHandlersFor("com.k_int.aggregator.event.upload",event_properties)
          if ( handler_to_invoke != null ) {
            handlerSelectionService.executeHandler(handler_to_invoke,event_properties)
            // response.code should be 0 == Processed, 1==In process, 2==Queued or Some other Error
          }
          else {
            response.code = '-2';
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
        de.errors.allErrors.each {
          log.error(it.defaultMessage)
        }
      }
    }
    else {
      response.code = '-1';
      response.status = 'Validation Error - No file uploaded';
      response.message = 'You must supply an upload parameter in the form of a multipart-file field';
    }


    render(view:"index",model:response)
  }

  def extract(temp_file, context_dir) {
    log.debug("Extract ${temp_file} into ${context_dir}");
    java.util.zip.ZipFile zf = new java.util.zip.ZipFile(temp_file)
    zf.entries().each { zfe ->
      log.debug("processing: " + zfe.getName());
      if ( zfe.isDirectory()) {
        // Assume directories are stored parents first then children.
        log.debug("Make directory ${zfe.getName()}");
        // def new_parent = new File(zfe.getName()).mkdir();
      } 
      else {
        log.debug("Extract ${zfe.getName()} to ${context_dir}/${zfe.getName()}");
        def bos = new BufferedOutputStream(new FileOutputStream("${context_dir}/${zfe.getName()}")) 
        bos << zf.getInputStream(zfe)
      }
      
    }
  }

  def createUser(username,password,name) {
    // Create example User account
    //def user = InstanceGenerator.user()
    //user.username = username;
    //user.pass = password;
    //user.passConfirm = password;
    //user.enabled = true

    //def userProfile = InstanceGenerator.profile()
    //userProfile.fullName = name
    //userProfile.owner = user
    //user.profile = userProfile

    //def savedUser = userService.createUser(user)

    //if (savedUser.hasErrors()) {
    //  savedUser.errors.each {
    //    log.error(it)
    //  }
      throw new RuntimeException("Error creating example user")
    //}
  }

}
