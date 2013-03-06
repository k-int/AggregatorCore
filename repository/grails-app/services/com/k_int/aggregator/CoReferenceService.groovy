package com.k_int.aggregator

class CoReferenceService {


    // Accept an array of identifers of the form [ [context:ctx, identifier:id], [context:ctx, identifier:id] ]
    // If any of the identifiers match existing entries, add any extra identifers to the coreference database for that item
    // otherwise create a new canonical identifier
    def resolve(provider, identifiers) {

        boolean matched = false;
        def resolve_response = [:]
        CanonicalIdentifier matched_with = null;
    
        identifiers.find { id ->

            def matched_identifiers = null
            log.debug("Trying to match ${id}");

            if ( ( id.idtype == null ) || ( id.idtype == 'undefined' ) ) {
                matched_identifiers = IdentifierInstance.withCriteria {
                    eq("identifierType","undefined")
                    eq("identifierValue",id.idvalue)
                    owner {
                        eq(owner, provider)
                    }
                }
            }
            else {
                matched_identifiers = IdentifierInstance.withCriteria {
                    eq("identifierType",id.idtype)
                    eq("identifierValue",id.idvalue)
                    owner {
                        eq("owner", provider)
                    }
                }
            }

            log.debug("Found identifiers: ${matched_identifiers}");

            if ( matched_identifiers.size() == 1 ) {
                matched = true
                matched_with = matched_identifiers[0].owner;
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
            resolve_response.reason='existing';
        }
        else {
            // No match found, create a new identifier
            log.debug("Not Matched - creating new canonical identifier")
            resolve_response.reason='new';

            def new_canonical_identifier = java.util.UUID.randomUUID().toString()
            matched_with = new CanonicalIdentifier(owner:provider,canonicalIdentifier:new_canonical_identifier).save(flush:true);
            new IdentifierInstance(identifierType:'__canonical',
                identifierValue:new_canonical_identifier,
                owner:matched_with).save(flush:true)
            identifiers.each { id ->
                if ( id.idtype == null ) {
                    new IdentifierInstance(identifierType:'undefined',identifierValue:id.idvalue,owner:matched_with).save(flush:true)
                }
                else {
                    new IdentifierInstance(identifierType:id.idtype,identifierValue:id.idvalue,owner:matched_with).save(flush:true)
                }
            }
        }

        resolve_response.canonical_identifier = matched_with

        resolve_response
    }
}
