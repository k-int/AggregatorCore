class UrlMappings {

	static mappings = {

               "/$controller" {
                   action = [GET:"index", POST:"save"] // PUT:"update", DELETE:"delete", POST:"save"]
                   constraints {
                     // apply constraints here
                   }
                }

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

                "/" (controller:"home", action:"index")

		"500"(view:'/error')
	}
}
