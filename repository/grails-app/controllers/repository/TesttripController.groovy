package repository

import grails.plugins.springsecurity.Secured

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class TesttripController {

    def tripleStoreService

    def index() { 
        tripleStoreService.update('''<?xml version="1.0"?>
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:mo="urn://museums/ma/v1/ma-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dc="http://purl.org/dc/elements/1.1/">
  <mo:resource rdf:about="urn:nmm:object-500560">
    <mo:idNumber>RIN/12/1</mo:idNumber>
    <dc:title>The Royal Indian Navy (1612-1947) Association</dc:title>
  </mo:resource>
</rdf:RDF>''',
                              'http://ianibbo.me/testuri',
                              'application/rdf');
                              
    }
}
