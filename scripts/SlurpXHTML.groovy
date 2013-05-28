
foo = new XmlSlurper().parse(new File('../data/binary/method|com.sun.tools.javadoc.SeeTagImpl|referencedMemberName|java.lang.String|0.html'))

//println foo


def methods = foo.body.'**'.findAll { it.@'class' == 'method' }

println "${methods.size()} methods"

methods.each { method ->
    def sentences = method.pre.Sentence
    println "method ${method.@id} has ${sentences.size()} sentences"
    sentences.eachWithIndex { s, i ->
        println "$i ${s.@'gate:gateId'}"
        println s.Token*.text()
        println '---'
    }
}

println foo.children().size()
