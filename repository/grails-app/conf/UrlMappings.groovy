class UrlMappings {

	static mappings = {

               "/admin/reindex" ( controller:"reindex", action:"index" )

               "/admin/$action?" {
                   // action = [GET:"index", POST:"save"] // PUT:"update", DELETE:"delete", POST:"save"]
                   controller = 'admin'
                   constraints {
                     // apply constraints here
                   }
                }

		"/$controller" {
                  action = [GET:"index", POST:"save"] // PUT:"update", DELETE:"delete", POST:"save"]
                }

		"/$controller/$id?/$action?" {
			constraints {
				// apply constraints here
			}
		}

                "/" (controller:"home", action:"index")

		"500"(view:'/error')
	}
}
