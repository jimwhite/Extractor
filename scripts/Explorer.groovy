import groovy.xml.StreamingMarkupBuilder
import org.ifcx.extractor.MethodData
import org.ifcx.extractor.util.Sexp

data_dir = new File("data")
methods_dir = new File(data_dir, "methods")
updates_dir = new File(data_dir, "method-updates")

methods_dir.mkdirs()
updates_dir.mkdirs()

get("/") {
    new StreamingMarkupBuilder().bind {
        html {
            head { title('ReadIt Query Explorer') }
            body {
                p "ReadIt Query Explorer"
                p { a(href:'/packages', 'Packages')  }
                p { a(href:'/readmes/INSTALL', 'READMES')  }
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
    response.sendRedirect("/methods/" + method_id + ".html")
}

