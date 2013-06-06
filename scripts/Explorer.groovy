import groovy.xml.StreamingMarkupBuilder
import org.ifcx.extractor.MethodData
import org.ifcx.extractor.util.Sexp

data_dir = new File("data")
methods_dir = new File(data_dir, "methods")
updates_dir = new File(data_dir, "method-updates")

methods_dir.mkdirs()
updates_dir.mkdirs()

get("/") {
    def method_ids = methods_dir.listFiles().grep { it.name =~ /.*\.html$/}.collect { it.name - ~/\.html$/ }

    if (method_ids) {
        response.sendRedirect(method_nav(method_ids.first()))
    } else {
        "NO METHODS!"
    }
}

get("/method_frame/:method_id") {
    def method_id = urlparams.method_id
    new StreamingMarkupBuilder().bind {
        html {
            head { title("Method Frame") }
            frameset(rows:"8%,*") {
                frame(src:"/method_nav/$method_id")
                frame(src:method_href(method_id))
            }
        }
    }.toString()
}

get("/method_nav/:method_id") {
    def method_id = urlparams.method_id

    def method_ids = methods_dir.listFiles().grep { it.name =~ /.*\.html$/}.collect { it.name - ~/\.html$/ }

    def method_index = method_ids.indexOf(method_id)

    new StreamingMarkupBuilder().bind {
        html {
            head { title('Method Navigation') }
            body {
                div {
                    a(href:method_nav(method_ids.first()), target:"_top", "First")
                    span(' ')
                    if (method_index > 0) a(href:method_nav(method_ids[method_index-1]), target:"_top", "Previous")
                    span(' ')
                    if (method_index + 1 < method_ids.size()) a(href:method_nav(method_ids[method_index+1]), target:"_top", "Next")
                    span(' ')
                    a(href:method_nav(method_ids.last()), target:"_top", "Last")
                }
                div {
                    span(method_id)
                }
            }
        }
    }.toString()
}

post("/methods/update-method/:method_id") {
//    println request
    println urlparams
    println params

    def method_id = urlparams.method_id

    new File(updates_dir, method_id + ".txt").write Sexp.printTree(Sexp.map_to_tree("METHOD_UPDATE", params))

    def method = MethodData.readMethod(methods_dir, method_id)

    params.each { k, v -> method[k] = v }

    MethodData.writeMethod(methods_dir, method_id, method)

//    HttpServletResponse res = response
    response.sendRedirect(method_href(method_id))
}

String method_nav(String method_id) {
    "/method_frame/" + method_id
}

String method_href(String method_id) {
    "/methods/" + method_id + ".html"
}

get("/old_home") {
    new StreamingMarkupBuilder().bind {
//        mkp.yieldUnescaped("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">")
        html {
            head { title('ReadIt Query Explorer') }
            frameset(cols:"20%,80%") {
                frame(src:"methods_index")
                frame(id:"method", src:"javadocs.css")
            }
        }
    }.toString()
}

get("/methods_index") {
    def methods = [:]
    methods_dir.eachFileMatch(~/.*\.html$/) { file ->
        def method = MethodData.readMethod(file)
        if (method) methods[method.Id] = file
    }


    new StreamingMarkupBuilder().bind {
        html {
            head { title('ReadIt Query Explorer') }
            body {
                methods.keySet().sort().each { method_id ->
                    div {
                        a(href:method_href(method_id), target:"method", method_id)
                    }
                }
            }
        }
    }.toString()
}

