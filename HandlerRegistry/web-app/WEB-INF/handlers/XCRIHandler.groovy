package com.k_int.repository.handlers

@GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')

@Grab(group='com.gmongo', module='gmongo', version='0.9.2')
@Grab(group='org.elasticsearch', module='elasticsearch-lang-groovy', version='1.1.0')

import com.gmongo.GMongo
import org.apache.commons.logging.LogFactory
import grails.converters.*

class XCRIHandler {

  private static final log = LogFactory.getLog(this)
  
  def desc_mappings = [
                              "metadataKeywords": ["xcri:metadataKeywords","xcriterms:metadataKeywords"],
                              "abstract": ["xcri:abstract","xcriTerms:abstract"],
                              "careerOutcome": ["xcri:careerOutcome","xcriTerms:careerOutcome","Career Outcome"],
                              "prerequisites": ["xcri:prerequisites","xcriTerms:prerequisites","Entry Profile"],
                              "indicativeResource": ["xcri:indicativeResource","xcriTerms:indicativeResource","Indicative Resource"],
                              "assessmentStrategy":["xcri:assessmentStrategy","xcriTerms:assessmentStrategy","Assessment Strategy"],
                              "aim":["xcri:aim","xcriTerms:aim","Aim","terms:topic"],
                              "learningOutcome":["xcri:learningOutcome","xcriTerms:learningOutcome","Learning Outcome"],
                              "syllabus": ["xcri:syllabus","xcriTerms:syllabus","Syllabus"],   
                              "support": ["xcri:support","xcriTerms:support","Support"],
                              "teachingStrategy": ["xcri:teachingStrategy","xcriTerms:teachingStrategy","Teaching Strategy"],
                              "structure": ["xcri:structure","xcriTerms:structure","Structure"],
                              "specialFeature": ["xcri:specialFeature","xcriTerms:specialFeature","Special Feature"],
                              "leadsTo": ["xcri:leadsTo","xcriTerms:leadsTo","Leads To"],
                              "requiredResource":["xcri:requiredResource","xcriTerms:requiredResource","Required Resource"],
                              "providedResource":["xcri:providedResource","xcriTerms:providedResource","Provided Resource"],
                              "policy":["xcri:policy","xcriTerms:policy","Policy"],
                              "regulations":["xcri:regulations","xcriTerms:regulations","Policy"]
                          ]  

  // This handler processes XCRI documents... After the handler is invoked, the local mongodb
  // will contain a new database called xcri and a collection called courses. If rest = true is configured in
  // /etc/mongodb.conf you can use a URL like http://localhost:28017/xcri/courses/ to enumerate all courses
  // The handler also inserts records into an elasticsearch cluster, using a courses collection, you can 
  // query this with URL's like 
  // http://localhost:9200/_all/course/_search?q=painting%20AND%20A2
  // Which will search all indexes (In ES, an index is like a solr core) for all items of type course with the given parameters.
  // This handler creates an index(core) with name courses, so to search this specific core:
  // http://localhost:9200/courses/course/_search?q=painting%20AND%20A2
  // Or search everything
  // http://localhost:9200/_search?q=painting%20AND%20A2

  // handlers have access to the repository mongo service.. suggest you use http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/
  def getHandlerName() {
    "XCRI_CAP"
  }

  def getRevision() {
    1
  }

  def getPreconditions() {
    [
      'p.rootElementNamespace=="http://xcri.org/profiles/catalog"'
    ]
  }

  def process(props, ctx) {

    log.debug("Validate....");
    if ( !validate() ) {
      log.debug("Validation failed...");
    }

    log.debug("process....");
    try {
  
      // Get hold of some services we might use ;)
      def mongo = new com.gmongo.GMongo();
      def db = mongo.getDB("xcri")
      // Object solrwrapper = ctx.getBean('SOLRWrapperService');
      Object eswrapper = ctx.getBean('ESWrapperService');
      Object gazetteer = ctx.getBean('gazetteerService');
      Object coreference = ctx.getBean('coReferenceService');
      Object termclient = ctx.getBean('terminologyClientService');

      org.elasticsearch.groovy.node.GNode esnode = eswrapper.getNode()
      org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
  
      log.debug("After call to getbean-eswrapper : ${eswrapper}");
  
      // Start processing proper
  
      props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"XCRI (CAP Profile) Document handler"])
  
      def d2 = props.xml.declareNamespace(['xcri':'http://xcri.org/profiles/catalog', 
                                         'xsi':'http://www.w3.org/2001/XMLSchema-instance',
                                         'xhtml':'http://www.w3.org/1999/xhtml',
                                         'dc':'http://purl.org/dc/elements/1.1/'])
  
      log.debug("root element namespace: ${d2.namespaceURI()}");
      log.debug("lookup namespace: ${d2.lookupNamespace('xcri')}");
      log.debug("d2.name : ${d2.name()}");

      d2.'xcri:provider'.each { provider ->

        def start_time = System.currentTimeMillis();
        def course_count = 0;

        def prov_title = provider.'xcri:title'.text()
        prov_title = prov_title? prov_title : provider.'xcri:name'.text() //if title not present try name
        
        def prov_uri = provider.'xcri:uri'.text()
        def prov_postcode = provider.'xcri:postcode'?.text()
        def prov_location = [:]

        if ( ( prov_postcode != null ) && ( prov_postcode.length() > 0 ) ) {
          def gaz_response = gazetteer.resolvePlaceName(prov_postcode);
          if ( ( gaz_response?.places != null ) && ( gaz_response.places.size() > 0 ) ) {
            log.debug("Geocoded provider postcode OK ${gaz_response.places[0]}");
            prov_location.lat = gaz_response.places[0].lat;
            prov_location.lon = gaz_response.places[0].lon;
          }
        }

        if ( ( prov_title == null ) || ( prov_title == '' ) ) {
          prov_title = "Missing Provider Title (${prov_uri})"
          props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'warn',msg:"Missing Provider Title (${prov_uri})"])
        }
    
        // Properties contain an xml element, which is the parsed document
        // def id1 = provider.'xcri:identifier'.text()
    
        // N.B. XCRI documents can have many identifiers...
        def identifiers = []
        def identifier_count = 0
        provider.'xcri:identifier'.each { id ->
          log.debug("Adding ${id.'@xsi:type'?.text()} : ${id.text()}");
          def identifier_value = id.text()
          if ( ( identifier_value != null ) && ( identifier_value.length() > 0 ) ) {
            identifiers.add( [ idtype:id.'@xsi:type'?.text(), idvalue:identifier_value ] )
          }
        }
  
        if ( identifiers.size() == 0 ) {
          props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'warn',msg:"XCRI Document contains no valid (document level) identifiers."])
          props.response.code=-6
          props.response.status="Data error processing document"
          props.response.message="The input document was missing a valid identifier at the top level. processing skipped"
          throw new Exception("No valid document identifier at top level");
        }
  
        def coreference_result = coreference.resolve(props.owner,identifiers)
        def canonical_identifier = coreference_result.canonical_identifier?.canonicalIdentifier;

        if ( coreference_result.reason == 'new' ) {
          log.debug("New provider.. register");
          def new_provider = [:];
          new_provider.identifier = canonical_identifier
          new_provider.label = prov_title
          new_provider.langlabel = [:]
          new_provider.langlabel['EN_uk'] = prov_title
          new_provider.url = prov_uri
          new_provider.lastModified = System.currentTimeMillis();
          new_provider.lat = prov_location.lat
          new_provider.lon = prov_location.lon
    
          db.providers.save(new_provider)
        }

        log.debug("Coreference service returns ${canonical_identifier} (${coreference_result.canonical_identifier})")
    
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Identifier for this XCRI document: ${canonical_identifier}"])
    
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Starting feed validation"])
    
        // Validation tests
    
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Validation complete. No fatal errors."])
    
        def prov_id = canonical_identifier
    
        provider.'xcri:course'.each { crs ->
    
          def crs_identifier = crs.'xcri:identifier'.text();
          def crs_internal_uri = "uri:${props.owner}:xcri:${canonical_identifier}:${crs_identifier}";
    
          log.debug("Processing course: ${crs_identifier}");
          props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Validating course entry ${crs_internal_uri} - ${crs.'xcri:title'}"]);
    
          log.debug("looking up course with identifier ${crs_internal_uri}");
          def course_as_pojo = db.courses.findOne(identifier: crs_internal_uri.toString())
    
          def mongo_action = "updated"
          if ( course_as_pojo != null ) {
            log.debug("Located existing record... updating course ${crs_identifier} internal GUID is ${course_as_pojo._id}");
          }
          else {
            mongo_action = "created"
            course_as_pojo = [:]
            // Gmongo driver doesn't seem good at passing back an _id, so we manually create one instead.
            // course_as_pojo._id = java.util.UUID.randomUUID().toString()
            // course_as_pojo._id = new com.mongodb.ObjectId()
            course_as_pojo._id = new org.bson.types.ObjectId()
            log.debug("No existing course information for ${crs_internal_uri}, create new record. new ID will be ${course_as_pojo._id}");
          }

          course_as_pojo.lastModified = System.currentTimeMillis()
    
          course_as_pojo.provid = prov_id
          course_as_pojo.provtitle = prov_title
          course_as_pojo.provloc = prov_location
          course_as_pojo.provuri = prov_uri
    
          course_as_pojo.identifier = crs_internal_uri.toString()
          course_as_pojo.title = crs.'xcri:title'?.text()?.toString() 
          course_as_pojo.imageuri = crs.'xcri:image'?.@src?.text()
    
          course_as_pojo.qual = [:]
          course_as_pojo.qual.type = crs.'xcri:qualification'.'xcri:type'?.text()
          course_as_pojo.qual.title = crs.'xcri:qualification'.'xcri:title'?.text()
          course_as_pojo.qual.description = crs.'xcri:qualification'.'xcri:description'?.text()
          course_as_pojo.qual.level = crs.'xcri:qualification'.'xcri:level'?.text()
          course_as_pojo.qual.awardedBy = crs.'xcri:qualification'.'xcri:awardedBy'?.text()
          course_as_pojo.qual.accreditedBy = crs.'xcri:qualification'.'xcri:accreditedBy'?.text()

          if ( course_as_pojo.qual.type ) {
            def qual_entry = termclient.resolve('qualtype',course_as_pojo.qual.type);
            course_as_pojo.qual.typeControlledTerm = qual_entry;
          }

          if ( course_as_pojo.qual.level ) {
            def level_entry = termclient.resolve('level',course_as_pojo.qual.level);
            course_as_pojo.qual.levelControlledTerm = level_entry.term
          }
          
          course_as_pojo.description = ''
          
          course_as_pojo.descriptions = [:]
  
          crs.'xcri:description'.each { desc ->
              
             if(desc.text()?.toString().length() > 0){
                 //append to core description
                 course_as_pojo.description += desc.text()?.toString() + ' '
             }
              
             if(desc.@'xsi:type') {
                 String desc_key = lookupDescMapping(desc.@'xsi:type'?.text())
                 
                 if(desc_key)                
                     course_as_pojo[desc_key] = desc?.text()?.toString();
                 else
                     course_as_pojo.descriptions[expandNamespacedLiteral(props.xml, desc.@'xsi:type'?.text())] = desc?.text()?.toString();
             }
             if(desc.@'type') { 
                 String desc_key = lookupDescMapping(desc.@'type'?.text())
                        
                 if(desc_key)                
                     course_as_pojo[desc_key] = desc?.text()?.toString();
                 else
                     course_as_pojo.descriptions[expandNamespacedLiteral(props.xml, desc.@'type'?.text())] = desc?.text()?.toString();
             }
            
          }
          
          course_as_pojo.credits = []
          
          crs.'xcri:credit'.each { cred ->       
              def credit = [:]
              credit.scheme = cred.'xcri:scheme'?.text()?.toString()
              credit.level = cred.'xcri:level'?.text()?.toString()
              credit.val = cred.'xcri:value'?.text()?.toString()
              course_as_pojo.credits << credit
          }
          
          if(crs.'xcri:presentation'){ 
              course_as_pojo.presentations = []
              
              crs.'xcri:presentation'.each { pres ->
                  def presentation = [:]
                  setIfPresent(pres.'xcri:identifier',presentation,'identifier')
                  setIfPresent(pres.'xcri:description',presentation,'description')
                  setIfPresent(pres.'xcri:cost',presentation,'cost')
                  setIfPresent(pres.'xcri:start',presentation,'start')
                  setIfPresent(pres.'xcri:end',presentation,'end')
                  setIfPresent(pres.'xcri:duration',presentation,'duration')
                  setIfPresent(pres.'xcri:applyFrom',presentation,'applicationsOpen')
                  setIfPresent(pres.'xcri:applyUntil',presentation,'applicationsClose')
                  setIfPresent(pres.'xcri:applyTo',presentation,'applyTo')
                  setIfPresent(pres.'xcri:enquireTo',presentation,'enquireTo')
                  setIfPresent(pres.'xcri:studyMode',presentation,'studyMode')
                  setIfPresent(pres.'xcri:attendanceMode',presentation,'attendanceMode')
                  setIfPresent(pres.'xcri:attendancePattern',presentation,'attendancePattern')
                  setIfPresent(pres.'xcri:languageOfInstruction',presentation,'languageOfInstruction')
                  setIfPresent(pres.'xcri:languageOfAssessment',presentation,'languageOfAssessment')
                  
                  if(pres.'xcri:venue'){
                      presentation.venue = [:]
                      def ven = pres.'xcri:venue'
                      setIfPresent(ven.'xcri:identifier',presentation.venue,'identifier')
                      setIfPresent(ven.'xcri:name',presentation.venue,'name')
                      setIfPresent(ven.'xcri:street',presentation.venue,'street')
                      setIfPresent(ven.'xcri:town',presentation.venue,'town')
                      setIfPresent(ven.'xcri:postcode',presentation.venue,'postcode')
                      setIfPresent(ven.'xcri:description',presentation.venue,'description')
                      setIfPresent(ven.'xcri:title',presentation.venue,'title')
                      setIfPresent(ven.'xcri:url',presentation.venue,'url')
                  }
                      
                  presentation.entryRequirements = []
                  
                  pres.'xcri:entryRequirements'.each { entryReq ->
                      if(entryReq.'xcri:entryRequirements'.'xcri:description') {
                          presentation.entryRequirements << entryReq.'xcri:description'.text()?.toString()
                      }
                      else {
                          presentation.entryRequirements << entryReq.text()?.toString()
                      }     
                  } 
        
                  course_as_pojo.presentations << presentation
              }
          }
                                    
          course_as_pojo.url = crs.'xcri:url'?.text()?.toString()
          course_as_pojo.subject = []
          course_as_pojo.subjectKeywords = []
          
          crs.'xcri:subject'.each { subj ->
            def subject_text = subj.text()?.toString();
            def term_entry = termclient.resolve('subject',subject_text);
            course_as_pojo.subject.add( term_entry.term )
          }
          
          crs.'dc:subject'.each { subj ->
              if(subj.@'xsi:type' && subj.@'xsi:type'?.text().equalsIgnoreCase("sfc:dpg")) { 
                def subject_text = subj.text()?.toString();
                def term_entry = termclient.resolve('subject',subject_text);
                course_as_pojo.subject.add( term_entry.term )
              }
              else if(subj.@'xsi:type' && subj.@'xsi:type'?.text().equalsIgnoreCase("asc:keyword")) { course_as_pojo.subjectKeywords.add(subj.text()?.toString()) }
              else { 
                def subject_text = subj.text()?.toString();
                def term_entry = termclient.resolve('subject',subject_text);
                course_as_pojo.subject.add( term_entry.term )
              }
          }

          if ( props['ulparam_feedStatus'] ) {
            course_as_pojo.recstatus = props['ulparam_feedStatus']
          }
          else {
            course_as_pojo.recstatus = 'private';
          }
    
          // def course_as_json = course_as_pojo as JSON;
          // log.debug("The course as JSON is ${course_as_json.toString()}");
          course_count++
    
          log.debug("Saving mongo instance of course....${crs_internal_uri}, _id=${course_as_pojo['_id']?.toString()}")
    
          // db.courses.update([identifier:crs_internal_uri.toString()],course_as_pojo, true);
          def mongo_store_result = db.courses.save(course_as_pojo)
    
          log.debug("After call to courses.save, response was, get _id is ${course_as_pojo['_id']?.toString()}")
    
          // Add an eventLog reponse that points to the entry for this course in the mongoDB
          props.response.eventLog.add([ts:System.currentTimeMillis(),
                                       type:"ref",
                                       serviceref:"mongo",
                                       mongoaction:mongo_action,
                                       mongodb:"xcri",
                                       mongoindex:"courses",
                                       mongotype:"course",
                                       mongoid:course_as_pojo._id?.toString()]);
    
          // Add an eventLog reponse that points to public XCRI Portal
          props.response.eventLog.add([ts:System.currentTimeMillis(),
                                       type:"ref",
                                       serviceref:"xcriportal",
                                       id:course_as_pojo._id?.toString()]);
    
          // Add an eventLog reponse that points to the entry for this course in the mongoDB
          props.response.eventLog.add([ts:System.currentTimeMillis(),
                                       type:"ref",
                                       serviceref:"es",
                                       escollection:"courses",
                                       estype:"course",
                                       esid:course_as_pojo._id?.toString()]);
    
    
          log.debug("Saved pojo. identifier will be \"${course_as_pojo['_id'].toString()}\"");
    
          if ( ( course_as_pojo != null ) && ( course_as_pojo['_id'] != null ) ) {
            // Mongo inserts an _id into the record.. we can reuse that
    
            log.debug("Sending record to es");
            try {
              def future = esclient.index {
                index "courses"
                type "course"
                id course_as_pojo['_id'].toString()
                source course_as_pojo
              }
              log.debug("Indexed respidx:$future.response.index/resptp:$future.response.type/respid:$future.response.id")
            }
            catch ( Exception e ) {
              log.error("Problem indexing record ${course_as_pojo['_id'].toString()}: ${e.message}");
              props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'error',msg:"Problem indexing record ${course_as_pojo['_id'].toString()}: ${e.message}"]);
            }
            finally {
            }
          }
          else {
            log.error("Failed to store course information ${course_as_pojo}");
            props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"There was an unexpected error trying to store the course information"]);
          }

          try {
            // Take a break so we don't thrash the CPU
            synchronized(this) {
              Thread.yield();
              Thread.sleep(500);
            }
          }
          catch ( Exception e ) {
          }
        }

        log.debug("Adding title ${prov_title} and resource identifier ${prov_id}");
        // These properties identify the processed file (resource) back to the coordination software
        props.response.title = prov_title
        props.response.resource_identifier = prov_id

        def elapsed = System.currentTimeMillis() - start_time
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Completed processing of ${course_count} courses from catalog ${canonical_identifier} for provider ${props.owner} in ${elapsed}ms"]);
      }
    }
    catch ( Exception e ) {
      log.error("Unexpected error",e);
      props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'error',msg:"Unexpected error ${e.message}"]);
      // -6 == Data error
    }
    finally {
      log.debug("XCRI handler complete");
    }

    
  }

  /**
   *  Initial handler installation, set up collections and other information
   */
  def setup(ctx) {
    log.debug("This is the XCRI handler setup method");

    Object eswrapper = ctx.getBean('ESWrapperService');
    // Object solrwrapper = ctx.getBean('SOLRWrapperService');

    org.elasticsearch.groovy.node.GNode esnode = eswrapper.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    // Get hold of mongodb 
    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("oda")

    // Get hold of an index admin client
    org.elasticsearch.groovy.client.GIndicesAdminClient index_admin_client = new org.elasticsearch.groovy.client.GIndicesAdminClient(esclient);

    // Create an index if none exists
    def future = index_admin_client.create {
      index 'courses'
    }

    // use http://localhost:9200/_all/_mapping to list all installed mappings

    // Declare a mapping of type "course" that explains to ES how it should index course elements
    log.debug("Attempting to put a mapping for course...");
    future = index_admin_client.putMapping {
      indices 'courses'
      type 'course'
      source {
        course {       // Think this is the name of the mapping within the type
          properties {
            provid {
              type = 'string'
              store = 'yes'
              index = 'not_analyzed'
            }
            title { // We declare a multi_field mapping so we can have a default "title" search with stemming, and an untouched title via origtitle
              type = 'multi_field'
              fields {
                title { 
                  type = 'string'
                  analyzer = 'snowball'
                }
                origtitle {
                  type = 'string'
                  store = 'yes'
                }
              }
            }
            subject {
              type = 'multi_field'
              fields {
                subject {
                  type = 'string'
                  store = 'yes'
                  index = 'not_analyzed'
                }
                subjectKw {
                  type = 'string'
                  analyzer = 'snowball'
                }
              }
            }
            provloc {
              type = 'geo_point'
            }
            level {
              type = 'string'
              index = 'not_analyzed'
            }
            studyMode {
              type = 'string'
              index = 'not_analyzed'
            }
            qual {
              properties {
                level {
                  type = 'string'
                  index = 'not_analyzed'
                }
              }
            }
            presentations {
              properties {
                start {
                  type = 'string'
                  index = 'not_analyzed'
                }
                startText {
                  type = 'string'
                  index = 'not_analyzed'
                }
                end {
                  type = 'string'
                  index = 'not_analyzed'
                }
                endText {
                  type = 'string'
                  index = 'not_analyzed'
                }
                applyTo {
                  type = 'string'
                  index = 'not_analyzed'
                }
                applyToText {
                  type = 'string'
                  index = 'not_analyzed'
                }
                enquireTo {
                  type = 'string'
                  index = 'not_analyzed'
                }
                enquireToText {
                  type = 'string'
                  index = 'not_analyzed'
                }
              }
            }
          }
        }
      }
    }
    log.debug("Installed course mapping ${future}");

    // Store a definition of the searchable part of the resource in mongo
    def courses_aggregation = db.aggregations.findOne(identifier: 'uri:aggr:cld:courses')

    if ( courses_aggregation == null ) {
      // Create a definition of a course CLD
      courses_aggregation = [:]
    }

    courses_aggregation.identifier = 'uri:aggr:cld:courses'
    courses_aggregation.type = 'es'
    courses_aggregation.indexes = ['courses']
    courses_aggregation.types = ['course']
    courses_aggregation.title = 'All UK Courses'
    courses_aggregation.description = 'An searchable aggregation of course descriptions from institutions in the UK'
    courses_aggregation.access_points = [ 
                                    [ field:'identifier', label:'Identifier' ], 
                                    [ field:'title', label:'Title' ], 
                                    [ field:'descriptions', label:'Description' ] ]
    db.aggregations.save(courses_aggregation);


    // Confirm SOLR setup for this aggregation
    // solrwrapper.verifyCore('courses');
  }

  def expandNamespacedLiteral(doc, literal) {
    def result = literal
    def colon_position = literal.indexOf(':')
    if ( colon_position > 0 ) {
      log.debug('literal contains a possible namespace')
      def candidate_namespace = literal.substring(0,colon_position)
      log.debug("Candidate namespace: ${candidate_namespace}")
      def expanded_namespace = doc.lookupNamespace(candidate_namespace);
      log.debug("Expanded namespace: ${expanded_namespace}")
      if ( ( expanded_namespace != null ) && ( expanded_namespace.length() > 0 ) ) { 
        def term_part = literal.substring(colon_position+1,literal.length())
        if ( term_part.startsWith('#') || term_part.startsWith('/') ) {
          result = "${expanded_namespace}${term_part}"
        }
        else {
          result = "${expanded_namespace}#${term_part}"
        }
      }
    }
    log.debug("returning ${result}")
    result
  }
  
  def lookupDescMapping(type_value) {
      def result
      desc_mappings.each { 
          k, v ->     
           
          if(v.contains(type_value)) {
              result = k
          }
      }     
      return result
    }

  def setIfPresent(gpathresult, obj, prop) {
    def result = gpathresult.text().toString()
    if ( result?.length() != 0 )
      obj[prop] = result
  }

  def validate() {
    // Commented out call to validation service at http://validator.xcri.co.uk/API/Test for now.
  }
}
