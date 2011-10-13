package com.k_int.repository.handlers

@Grab(group='com.gmongo', module='gmongo', version='0.5.1')

import com.gmongo.GMongo
import org.apache.commons.logging.LogFactory
import grails.converters.*



class XCRIHandler {

  private static final log = LogFactory.getLog(this)

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
    log.debug("this is a doodah");

    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("xcri")

    def start_time = System.currentTimeMillis();
    def course_count = 0;

    props.response.messageLog.add("This is a message from the downloaded XCRI handler")

    def d2 = props.xml.declareNamespace(['xcri':'http://xcri.org/profiles/catalog', 
                                         'xsi':'http://www.w3.org/2001/XMLSchema-instance',
                                         'xhtml':'http://www.w3.org/1999/xhtml'])

    // def xcri = new groovy.xml.Namespace("http://xcri.org/profiles/catalog", 'xcri') 

    log.debug("root element namespace: ${d2.namespaceURI()}");
    log.debug("lookup namespace: ${d2.lookupNamespace('xcri')}");
    log.debug("d2.name : ${d2.name()}");

    // Properties contain an xml element, which is the parsed document
    def id1 = d2.'xcri:provider'.'xcri:identifier'.text()

    props.response.messageLog.add("Identifier for this XCRI document: ${id1}")

    d2.'xcri:provider'.'xcri:course'.each { crs ->

      def crs_identifier = crs.'xcri:identifier'.text();
      def crs_internal_uri = "uri:${props.owner}:xcri:${id1}:${crs_identifier}";

      log.debug("Processing course: ${crs_identifier}");
      props.response.messageLog.add("Validating course entry ${crs_internal_uri} - ${crs.'xcri:title'}");

      log.debug("looking up course with identifier ${crs_internal_uri}");
      def course_as_pojo = db.courses.findOne(identifier: crs_internal_uri.toString())

      if ( course_as_pojo != null ) {
        log.debug("Located existing record... updating");
      }
      else {
        log.debug("No existing course information for ${crs_internal_uri}, create new record");
        course_as_pojo = [:]
      }

      course_as_pojo.identifier = crs_internal_uri.toString();
      course_as_pojo.title = crs.'xcri:title'?.text()?.toString();
      course_as_pojo.descriptions = [:]
      crs.'xcri:description'.each { desc ->
        course_as_pojo.descriptions[desc.@'xsi:type'] = desc?.text()?.toString();
      }
      course_as_pojo.url = crs.'xcri:url'?.text()?.toString()
      course_as_pojo.subject = []
      crs.'subject'.each { subj ->
        course_as_pojo.subject.add( subj.text()?.toString() );
      }

      // def course_as_json = course_as_pojo as JSON;
      // log.debug("The course as JSON is ${course_as_json.toString()}");
      course_count++

      log.debug("Saving mongo instance of course....${crs_internal_uri}");
      // db.courses.update([identifier:crs_internal_uri.toString()],course_as_pojo, true);
      db.courses.save(course_as_pojo);
    }

    def elapsed = System.currentTimeMillis() - start_time;

    props.response.messageLog.add("Completed processing of ${course_count} courses from catalog ${id1} for provider ${props.owner} in ${elapsed}ms");
  }
}
