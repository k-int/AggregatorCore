package com.k_int.aggregator

class CoReferenceService {


  // Accept an array of identifers of the form [ [context:ctx, identifier:id], [context:ctx, identifier:id] ]
  // If any of the identifiers match existing entries, add any extra identifers to the coreference database for that item
  // otherwise create a new canonical identifier
  def resolve(provier, identifiers) {

    boolean matched = false;
    CanonicalIdentifier matched_with = null;
    
    identifiers.find { id ->

      def matched_identifiers = null
      log.debug("Trying to match ${id}");

      if ( ( id.idtype == null ) || ( id.idtype == 'undefined' ) ) {
        matched_identifiers = IdentifierInstance.withCriteria {
          eq("identifierType","undefined")
          eq("identifierValue",id.idvalue)
          owner {
            eq(owner, provier)
          }
        }
      }
      else {
        matched_identifiers = IdentifierInstance.withCriteria {
          eq("identifierType",id.idtype)
          eq("identifierValue",id.idvalue)
          owner {
            eq("owner", provier)
          }
        }
      }

      log.debug("Found identifiers: ${matched_identifiers}");

      if ( matched_identifiers.size() == 1 ) {
        matched = true
        matched_with = matched_canonical[0].owner;
      }
      else if ( matched_identifiers.size() > 1 ) {
        log.warn("Matched multiple identifiers.. Should not happen ${provider} ${identifiers}");
      }

      // If still not matched, loop, otherwise move to next (Find iterates until it's last evaluation == true)
      matched
    }

    if ( matched ) {
      // Match found, return the canonical identifier
      log.debug("Matched")
    }
    else {
      // No match found, create a new identifier
      log.debug("Not Matched")
    }

    matched_with
  }
}
