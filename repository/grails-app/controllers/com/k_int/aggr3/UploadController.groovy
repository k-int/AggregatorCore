package com.k_int.aggr3

class UploadController {

    def index = { 
      println "Index...."
      // Empty response object
      def response = ["testprop":"testvalue"]
      response
    }

    def save = { 
      println "Save...."
      def response = ["testprop":"testvalue"]
      render(view:"index",model:response)
    }
}
