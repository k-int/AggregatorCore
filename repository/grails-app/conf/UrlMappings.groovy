class UrlMappings {

	static mappings = {

               "/admin/reindex" ( controller:"reindex", action:"index" )

               "/$controller" {
                   action = [GET:"index", POST:"save"] // PUT:"update", DELETE:"delete", POST:"save"]
                   constraints {
                     // apply constraints here
                   }
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
