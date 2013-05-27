import groovy.xml.StreamingMarkupBuilder

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
