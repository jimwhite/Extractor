import org.ifcx.extractor.PrettyPrinter
import org.ifcx.extractor.util.Sexp

//foo = new XmlSlurper().parse(new File('../data/methods/method|com.sun.tools.javac.jvm.ClassReader|isAsciiDigit|boolean|1|char.html'))
foo = new XmlSlurper().parse(new File('../data/methods/method|com.sun.tools.doclets.formats.html.markup.HtmlWriter|getBold|java.lang.String|0.html'))

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

def extracts = foo.body.'**'.findAll { it.@'class' == 'method-extract' }

println "${extracts.size()} extracts"

extracts.each { extract ->
    def extract_text = extract.text()

    println extract_text
    def tree = Sexp.read_one_sexp(new StringReader(extract_text))
    println tree
    def m = Sexp.tree_to_map(tree)
    println m.Comment
    println m.Sentence
}
