package com.k_int.aggr3

import grails.test.*
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.mock.web.MockMultipartFile

import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder




class UploadTests extends ControllerUnitTestCase {

    def grailsApplication
    // def controller

    protected void setUp() {
        super.setUp()
        // controller = grailsApplication.mainContext[SomeController.name]
        // controller.metaClass.request = new MockMultipartHttpServletRequest()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
      // controller.params.owner = "marcpalmer"
      // controller.params.on_behalf_of = "secret"
      // controller.params.passwordConfirm = "secret"
      // controller.signup()
    }

    // def testUpload() { 
    //     mockRequest.contentType ="multipart/form-data" 
    //     mockRequest.method = "POST" 
    //     mockParams.owner="fred"
    //     mockParams.on_behalf_of="fred"
    //     mockParams.owner="fred"
        // controller.request.addFile(new MockMultipartFile('file', 'something.jpg', 'image/jpeg', "123" as byte[]))
    //     def response = controller.save()             
    // } 
}
