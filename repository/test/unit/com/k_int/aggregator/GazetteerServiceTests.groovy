package com.k_int.aggregator



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GazetteerService)
class GazetteerServiceTests {


    void testReverseGeocode() {
        def mongoService = new MongoService()
        def gazetteerService = new GazetteerService()
        gazetteerService.mongoService = mongoService
        mongoService.startup()
        def rev_lookup = gazetteerService.reverseGeocode('53.2281100000','-0.5499100000')
        assertEquals 'Check Geocode of 53.2281100000,-0.5499100000 returns county Lincolnshire', 'Lincolnshire', rev_lookup.county
    }
    void testGoogleGeocode(){
        def mongoService = new MongoService()
        def newgazService = new NewGazService()
        newgazService.mongoService = mongoService
        mongoService.startup()
        
        def lookup = newgazService.processedGeocode('S3 8PZ')
        assertEquals 'Check Geocode of S3 8PZ returns', 'South Yorkshire', lookup.county
    }
}
