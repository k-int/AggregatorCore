package com.k_int.aggr3.admin

import org.springframework.http.ResponseEntity
import grails.converters.*
import com.k_int.aggregator.*
import groovy.json.JsonSlurper


class CoReferenceController 
{
    def index = {
    }
    
    def exporter = 
    {
        def response = [canonicalIdentifierList : CanonicalIdentifier.list(params)]
        render response as JSON
    }
    
    def upload =
    {        
        def response = [:]
        
        def f = request.getFile('myFile')
        
        if (f.empty)
        {          
            response.message = 'file cannot be empty';
        }
        else
        {
    
            def json_list = new JsonSlurper().parseText( f.inputStream.text )
           
            json_list?.canonicalIdentifierList.each
            {
                def objInstance = CanonicalIdentifier.get(it.id)
                
                if(objInstance)//if exists update identifier
                {
                    objInstance.canonicalIdentifier = it.canonicalIdentifier
                    objInstance.save(flush: true)
                }
                else //create new
                {     
                    objInstance = new CanonicalIdentifier(it)
                    objInstance.save(flush: true)
                }
            }
            
            response.message = 'Import complete';
        }
    
        render(view:"index",model:response)
    }
}
