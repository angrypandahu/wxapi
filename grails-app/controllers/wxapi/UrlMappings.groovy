package wxapi

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(redirect: "/wx")
        "/adminIndex"(view: "/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
