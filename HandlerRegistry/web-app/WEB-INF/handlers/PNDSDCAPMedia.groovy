package com.k_int.repository.handlers

@GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')

@Grab(group='com.gmongo', module='gmongo', version='0.9.2')
@Grab(group='org.elasticsearch', module='elasticsearch-lang-groovy', version='0.17.8')

import com.gmongo.GMongo
import org.apache.commons.logging.LogFactory
import grails.converters.*

class PNDSDCAPMedia {

  private static final log = LogFactory.getLog(this)

  // This handler processes PNDS DCAP documents... After the handler is invoked, the local mongodb
  // will contain a new database called frbr and a collection called default. If rest = true is configured in
  // /etc/mongodb.conf you can use a URL like http://localhost:28017/frbr/default/ to enumerate all items
  // The handler also inserts records into an elasticsearch cluster, using a courses collection, you can 
  // query this with URL's like 
  // http://localhost:9200/_all/frbr/_search?q=painting%20AND%20A2
  // Which will search all indexes (In ES, an index is like a solr core) for all items of type course with the given parameters.
  // This handler creates an index(core) with name courses, so to search this specific core:
  // http://localhost:9200/frbr/default/_search?q=painting%20AND%20A2
  // Or search everything
  // http://localhost:9200/_search?q=painting%20AND%20A2

  // Some notes on FRBR interpretations
  // Work - abstract definition of the subject
  // Expression - Photographic information
  // Manifestation - Thumbnails, Primary Items, Alternate Formats
  // Item - Instance Information - Specific identified copies.

  // handlers have access to the repository mongo service.. suggest you use http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/
  def getHandlerName() {
    "PNDS_DCAP_Media"
  }

  def getRevision() {
    1
  }

  def getPreconditions() {
    [
      'p.rootElementNamespace=="http://www.peoplesnetwork.gov.uk/schema/CultureGrid_Item"'
    ]
  }

  def process(props, ctx) {
    log.debug("process....");

    def start_time = System.currentTimeMillis();
    def course_count = 0;

    // Get hold of some services we might use ;)
    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("frbr")
    Object eswrapper = ctx.getBean('ESWrapperService');
    org.elasticsearch.groovy.node.GNode esnode = eswrapper.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    // Co referencing service
    // Object coref_service = ctx.getBean('coReferenceService');

    log.debug("After call to getbean-eswrapper : ${eswrapper}");

    // Start processing proper

    props.response.eventLog.add([type:"msg",msg:"This is a message from the downloaded PNDS DCAP (Media) handler"])

    def d2 = props.xml.declareNamespace(['xsi':'http://www.w3.org/2001/XMLSchema-instance',
                                         'xhtml':'http://www.w3.org/1999/xhtml',
                                         'dcterms':'http://purl.org/dc/terms/',
                                         'e20cl':'http://www.20thcenturylondon.org.uk',
                                         'pnds_dc':'http://purl.org/mla/pnds/pndsdc/',
                                         'pndsterms':'http://purl.org/mla/pnds/terms/',
                                         'dc':'http://purl.org/dc/elements/1.1/',
                                         'culturegrid_item':'http://www.peoplesnetwork.gov.uk/schema/CultureGrid_Item'])

    log.debug("root element namespace: ${d2.namespaceURI()}");
    log.debug("lookup namespace: ${d2.lookupNamespace('xcri')}");
    log.debug("d2.name : ${d2.name()}");

    // Properties contain an xml element, which is the parsed document
    def id1 = d2.'dc:identifier'.text()

    props.response.eventLog.add([type:"msg",msg:"Identifier for this PNDS_DCAP document: ${id1}"])

    log.debug("looking up work with identifier ${id1}");
    // coref_service.registerIdentifier(id1);

    def work_information = db.work.findOne(identifier: id1.toString())
    def expression_information = db.expression.findOne(identifier: id1.toString())

    if ( work_information == null ) 
      work_information = [:]

    if ( expression_information == null ) 
      expression_information = [:]

    work_information._id = java.util.UUID.randomUUID().toString()
    work_information.identifier = id1.toString();
    work_information.title = d2.'dc:title'?.text()?.toString();

    expression_information._id = java.util.UUID.randomUUID().toString()
    expression_information.pns_identifier = id1.toString();
    expression_information.work_id = work_information._id;

    log.debug("Saving expression instance: ${expression_information._id}, work instance:${work_information._id}");

    db.expression.save(expression_information);
    db.work.save(work_information);

    def elapsed = System.currentTimeMillis() - start_time
    props.response.eventLog.add([type:"msg",msg:"Completed processing of PNDS_DCAP encoded resource identified by ${id1}"]);

    // create the denormalised retrieval records (Work and Manifestation)
  }

  def setup(ctx) {
    log.debug("This is the PNDS DCAP (Media) handler setup method");
    Object eswrapper = ctx.getBean('ESWrapperService');
    org.elasticsearch.groovy.node.GNode esnode = eswrapper.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    // Get hold of mongodb 
    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("oda")

    // Get hold of an index admin client
    org.elasticsearch.groovy.client.GIndicesAdminClient index_admin_client = new org.elasticsearch.groovy.client.GIndicesAdminClient(esclient);

  }
}
