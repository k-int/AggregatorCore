package com.k_int.aggregator



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GazetteerService)
class GazetteerServiceTests {


    void testReverseGeocode() {
      def gazetteerService = new GazetteerService()
      def rev_lookup = gazetteerService.reverseGeocode('53.2281100000','-0.5499100000')
      if ( ! rev_lookup.county == 'Lincolnshire' )
        fail "Reverse Lookup Failed"
    }
}
