package com.k_int.repository.handlers

@GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')

@Grab(group='com.gmongo', module='gmongo', version='0.9.2')
@Grab(group='org.elasticsearch', module='elasticsearch-lang-groovy', version='1.0.0')

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
    log.debug("process....");
    try {
  
      // Get hold of some services we might use ;)
      def mongo = new com.gmongo.GMongo();
      def db = mongo.getDB("xcri")
      // Object solrwrapper = ctx.getBean('SOLRWrapperService');
      Object eswrapper = ctx.getBean('ESWrapperService');
      Object coreference = ctx.getBean('coReferenceService');
      org.elasticsearch.groovy.node.GNode esnode = eswrapper.getNode()
      org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
  
      log.debug("After call to getbean-eswrapper : ${eswrapper}");
  
      // Start processing proper
  
      props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"XCRI (CAP Profile) Document handler"])
  
      def d2 = props.xml.declareNamespace(['xcri':'http://xcri.org/profiles/catalog', 
                                           'xsi':'http://www.w3.org/2001/XMLSchema-instance',
                                           'xhtml':'http://www.w3.org/1999/xhtml',
                                           'dc':'http://purl.org/dc/elements/1.1/'])
  
      // def xcri = new groovy.xml.Namespace("http://xcri.org/profiles/catalog", 'xcri') 
  
      log.debug("root element namespace: ${d2.namespaceURI()}");
      log.debug("lookup namespace: ${d2.lookupNamespace('xcri')}");
      log.debug("d2.name : ${d2.name()}");

      d2.'xcri:provider'.each { provider ->

        def start_time = System.currentTimeMillis();
        def course_count = 0;
    
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
  
        def canonical_identifier = coreference.resolve(props.owner,identifiers)
        log.debug("Coreference service returns ${canonical_identifier} (${canonical_identifier.canonicalIdentifier})")
    
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Identifier for this XCRI document: ${canonical_identifier}"])
    
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Starting feed validation"])
    
        // Validation tests
    
        props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"Validation complete. No fatal errors."])
    
        def prov_id = canonical_identifier.canonicalIdentifier
        def prov_title = provider.'xcri:title'.text()
        def prov_uri = provider.'xcri:uri'.text()
    
        provider.'xcri:course'.each { crs ->
    
          def crs_identifier = crs.'xcri:identifier'.text();
          def crs_internal_uri = "uri:${props.owner}:xcri:${canonical_identifier.canonicalIdentifier}:${crs_identifier}";
    
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
    
          course_as_pojo.provid = prov_id
          course_as_pojo.provtitle = prov_title
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
          
          course_as_pojo.descriptions = [:]
  
          crs.'xcri:description'.each { desc ->
              
             if(desc.@'xsi:type')
             {
                 String desc_key = lookupDescMapping(desc.@'xsi:type'?.text())
                 
                 if(desc_key)                
                     course_as_pojo[desc_key] = desc?.text()?.toString();
                 else
                     course_as_pojo.descriptions[expandNamespacedLiteral(props.xml, desc.@'xsi:type'?.text())] = desc?.text()?.toString();
             }
             else if(desc.@'type')
             { 
                 String desc_key = lookupDescMapping(desc.@'type'?.text())
                        
                 if(desc_key)                
                     course_as_pojo[desc_key] = desc?.text()?.toString();
                 else
                     course_as_pojo.descriptions[expandNamespacedLiteral(props.xml, desc.@'type'?.text())] = desc?.text()?.toString();
             }
             else
             {
                 course_as_pojo.description = desc.text()?.toString();
             }     
             
                
             
//            String desc_type_lit = desc.@'xsi:type'?.text()
//            // String desc_type = desc.@'xsi:type'?.text()?.replaceAll(':','_').toString()
//            String desc_type = expandNamespacedLiteral(props.xml, desc_type_lit)
//  
//            log.debug("Processing description ${desc_type} from ${desc_type_lit}");
//    
//            if ( ( desc_type != null ) && 
//                 ( desc_type.length() > 0 ) &&
//                 ( desc.text() != null ) &&
//                 ( desc.text().length() > 0 ) ) {
//              switch ( desc_type ) {
//                case 'http://xcri.org/profiles/catalog#metadataKeywords': course_as_pojo.keywords = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#abstract': course_as_pojo.courseAbstract = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#careerOutcome': course_as_pojo.careerOutcome = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#prerequisites': course_as_pojo.prerequisites = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#indicativeResource': course_as_pojo.indicativeResource = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#assessmentStrategy': course_as_pojo.assessmentStrategy = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#aim': course_as_pojo.aim = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog#learningOutcome': course_as_pojo.learningOutcome = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#support': course_as_pojo.support = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#teachingStrategy': course_as_pojo.teachingStrategy = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#aim': course_as_pojo.aim = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#structure': course_as_pojo.structure = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#specialFeature': course_as_pojo.specialFeature = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#assessmentStrategy': course_as_pojo.assessmentStrategy = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#leadsTo': course_as_pojo.leadsTo = desc?.text()?.toString(); break;
//                case 'http://xcri.org/profiles/catalog/terms#requiredResource': course_as_pojo.requiredResource = desc?.text()?.toString(); break;
//                default:
//                  log.debug("Unhandled description type : ${desc_type}");
//                  course_as_pojo.descriptions[desc_type] = desc?.text()?.toString();
//                  break;
//              }
//            }
//            else {
//            }
            
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
                  presentation.identifier = elementStringOrNull(pres.'xcri:identifier')
                  presentation.description = elementStringOrNull(pres.'xcri:description')
                  presentation.cost = elementStringOrNull(pres.'xcri:cost')
                  presentation.start = elementStringOrNull(pres.'xcri:start')
                  presentation.end = elementStringOrNull(pres.'xcri:end')
                  presentation.duration = elementStringOrNull(pres.'xcri:duration')
                  presentation.applicationsOpen = elementStringOrNull(pres.'xcri:applyFrom')
                  presentation.applicationsClose = elementStringOrNull(pres.'xcri:applyUntil')
                  presentation.applyTo = elementStringOrNull(pres.'xcri:applyTo')
                  presentation.enquireTo = elementStringOrNull(pres.'xcri:enquireTo')
                  presentation.studyMode = elementStringOrNull(pres.'xcri:studyMode')
                  presentation.attendanceMode = elementStringOrNull(pres.'xcri:attendanceMode')
                  presentation.attendancePattern = elementStringOrNull(pres.'xcri:attendancePattern')
                  presentation.languageOfInstruction = elementStringOrNull(pres.'xcri:languageOfInstruction')
                  presentation.languageOfAssessment = elementStringOrNull(pres.'xcri:languageOfAssessment')
                  
                  if(pres.'xcri:venue'){
                      presentation.venue = [:]
                      presentation.venue.identifier = elementStringOrNull(pres.'xcri:venue'.'xcri:identifier')
                      presentation.venue.name = elementStringOrNull(pres.'xcri:venue'.'xcri:name')
                      presentation.venue.street = elementStringOrNull(pres.'xcri:venue'.'xcri:street')
                      presentation.venue.town = elementStringOrNull(pres.'xcri:venue'.'xcri:town')
                      presentation.venue.postcode = elementStringOrNull(pres.'xcri:venue'.'xcri:postcode')
                      presentation.venue.description = elementStringOrNull(pres.'xcri:venue'.'xcri:description')
                      presentation.venue.title = elementStringOrNull(pres.'xcri:venue'.'xcri:title')
                      presentation.venue.url = elementStringOrNull(pres.'xcri:venue'.'xcri:url')
                  }
                      
                  presentation.entryRequirements = []
                  
                  pres.'xcri:entryRequirements'.each { entryReq ->
                      if(entryReq.'xcri:entryRequirements'.'xcri:description')
                      {
                          presentation.entryRequirements << entryReq.'xcri:description'.text()?.toString()
                      }
                      else
                      {
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
            course_as_pojo.subject.add( subj.text()?.toString() )
          }
          
          crs.'dc:subject'.each { subj ->
              if(subj.@'xsi:type' && subj.@'xsi:type'?.text().equalsIgnoreCase("sfc:dpg")) { course_as_pojo.subject.add(subj.text()?.toString()) }
              else if(subj.@'xsi:type' && subj.@'xsi:type'?.text().equalsIgnoreCase("asc:keyword")) { course_as_pojo.subjectKeywords.add(subj.text()?.toString()) }
              else { course_as_pojo.subject.add(subj.text()?.toString()) }
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
            def future = esclient.index {
              index "courses"
              type "course"
              id course_as_pojo['_id'].toString()
              source course_as_pojo
            }
            log.debug("Indexed respidx:$future.response.index/resptp:$future.response.type/respid:$future.response.id")
          }
          else {
            log.error("Failed to store course information ${course_as_pojo}");
            props.response.eventLog.add([ts:System.currentTimeMillis(),type:'msg',lvl:'info',msg:"There was an unexpected error trying to store the course information"]);
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

    // use http://localhost:9200/_all/_mapping to list all installed mappings

    // Declare a mapping of type "course" that explains to ES how it should index course elements
    log.debug("Attempting to put a mapping for course...");
    def future = index_admin_client.putMapping {
      indices 'courses'
      type 'course'
      source {
        course {       // Think this is the name of the mapping within the type
          properties {
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
            provid {
              type = 'string'
              store = 'yes'
              index = 'not_analyzed'
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

  def elementStringOrNull(gpathresult) {
    def result = gpathresult.text().toString()
    if ( result?.length() == 0 )
      result = null
    result
  }
}
