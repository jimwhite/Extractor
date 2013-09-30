import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import org.ifcx.extractor.DMRS
import org.ifcx.extractor.MethodData
import org.ifcx.extractor.util.Sexp

import java.text.SimpleDateFormat

data_dir = new File("data")
methods_dir = new File(data_dir, "methods")
updates_dir = new File(data_dir, "method-updates")

methods_dir.mkdirs()
updates_dir.mkdirs()

parses_dir = new File("tmp/parses")

comment_index = MethodData.readCommentIndex(new File(data_dir, "comments-index.txt"))

get("/") {
    def method_ids = methods_dir.listFiles().grep { it.name =~ /.*\.html$/}.collect { it.name - ~/\.html$/ }

    if (method_ids) {
        response.sendRedirect(method_frame(method_ids.first()))
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
    }
}

get("/method_nav/:method_id") {
    String method_id = urlparams.method_id

    def method_ids = methods_dir.listFiles().grep { it.name =~ /.*\.html$/}.collect { it.name - ~/\.html$/ }

    def method_index = method_ids.indexOf(method_id)

    def method = MethodData.readMethod(methods_dir, method_id)

    new StreamingMarkupBuilder().bind {
        html {
            head {
                title('Method Navigation')
                link(rel:"stylesheet", href:"/javadocs.css")
            }
            body {
                div {
                    a(href:method_frame(method_ids.first()), target:"_top", "First")
                    span(' ')
                    if (method_index > 0) a(href:method_frame(method_ids[method_index-1]), target:"_top", "Previous")
                    span(' ')
                    if (method_index + 1 < method_ids.size()) a(href:method_frame(method_ids[method_index+1]), target:"_top", "Next")
                    span(' ')
                    a(href:method_frame(method_ids.last()), target:"_top", "Last")
                }
                div {
                    span('class':'method-id', method_id)
                }
                div {
                    code(method.Enclosure)
                    span(' ')
                    code(method.Name)
                }
            }
        }
    }
}

get("/method/:method_id") {
    def method_id = urlparams.method_id

    def method = MethodData.readMethod(methods_dir, method_id)

    def dmrs = DMRS.readParse(parses_dir, comment_index[method_id])

    markup {

        html(lang:"en", xmlns:"http://www.w3.org/1999/xhtml", 'xmlns:gate':"urn:gate:fakeNS") {
            head {
                title(method_id)
                link(rel:"stylesheet", href:"/javadocs.css")
            }
            body {
                form('class':'method', id:method_id, action:"update-method/${method_id}", method:'post') {
                    div('class':'method-id', method_id)
                    div {
                        def radio1 = {
                            if ((method.ConstituentJudgement ?: 'Unknown') == it)
                                input(type:'radio', name:'ConstituentJudgement', value:it, checked:true, it)
                            else
                                input(type:'radio', name:'ConstituentJudgement', value:it, it)
                        }
                        label('Constituent') {
                            radio1('Unknown')
                            radio1('S')
                            radio1('VP')
                            radio1('NP')
                        }
                    }
                    div {
                        def radio = {
                            if ((method.Judgement ?: 'Unknown') == it)
                                input(type:'radio', name:'Judgement', value:it, checked:true, it)
                            else
                                input(type:'radio', name:'Judgement', value:it, it)
                        }
                        label('Judgement') {
                            radio('Unknown')
                            radio('Pedantic')
                            radio('SomewhatPedantic')
                            radio('NotPedantic')
                            input(type:'submit')
                        }
                    }

    //                        if (method.Comment)
                    label('Comment') {
                        input('class':'method-comment', type:'text', name:'Comment', value:method.Comment)
                    }
    //                        else
    //                            p("No comment")

                    label('Note') {
                        input('class':'method-note', type:'text', name:'Note', value:method.Note)
                    }

                    pre(method.JavaSource)

    //                        if (method.Comment0) div('class':'method-comment-original', method.Comment0)

                    if (method.Sentences) {
                        ol('class':'method-sentences') {
                            method.Sentences.each { sent -> li('class':'method-sentence', sent) }
                        }
                    }
                }
                pre('class':'method-extract', Sexp.printTree(Sexp.map_to_tree(method)))
                if (dmrs) {
                    if (dmrs.size() > 1) p("MULTIPLE TOP NODES")
                    dmrs.each { a_dmrs -> pre('class':'method-dmrs', Sexp.printTree(a_dmrs)) }
                } else {
                    p('NO DMRS')
                }

                pre(DMRS.readXML(parses_dir, comment_index[method_id]))
            }
        }
    }
}

post("/method/update-method/:method_id") {
//    println request
    println urlparams
    println params

    def method_id = urlparams.method_id

    def method = MethodData.readMethod(methods_dir, method_id)

    def date_formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    def method_update = [Annotator:"JPW", AnnotationDate:date_formatter.format(new Date())]
    params.each { k, v ->
        method_update[k] = v
        if (method.containsKey(k) && (method[k] != v)) { method_update["PreviousValueOf" + k] = method[k] }
    }

    def updateFile = new File(updates_dir, method_id + ".txt")

    updateFile.append('\n' + Sexp.printTree(Sexp.map_to_tree("METHOD_UPDATE", method_update)) + '\n')

    method_update.each { k, v -> method[k] = v }

    MethodData.writeMethod(methods_dir, method_id, method)

//    HttpServletResponse res = response
    response.sendRedirect(method_href(method_id))
}

get("/comments/:judgement") {
    def comments = comment_index.collect { method_id, sent_num ->
        def method = MethodData.readMethod(methods_dir, method_id)
        [method.Judgement, method.Comment]
    }

    def judgement = urlparams.judgement

    if (judgement) {
        comments = comments.grep { it[0] == judgement }
    }

    markup {
        html(lang:"en", xmlns:"http://www.w3.org/1999/xhtml", 'xmlns:gate':"urn:gate:fakeNS") {
            head {
                title((judgement ?: "") + " Javadoc Comments")
                link(rel:"stylesheet", href:"/javadocs.css")
            }
            body {
                pre(comments.collect { it[1] }.join('\n'))
            }
        }
    }
}

get("/unjudged") {
    def comments = comment_index.collect { method_id, sent_num ->
        def method = MethodData.readMethod(methods_dir, method_id)
        [method.Judgement, method.Id, method.Comment]
    }

    comments = comments.grep { !it[0] && ((it[2])?.trim()) }

    markup {
        html(lang:"en", xmlns:"http://www.w3.org/1999/xhtml", 'xmlns:gate':"urn:gate:fakeNS") {
            head {
                title("Unjudged Javadoc Comments")
                link(rel:"stylesheet", href:"/javadocs.css")
            }
            body {
//                pre(comments.collect { it[1] }.join('\n'))
                table {
                    comments.each { comment ->
                        tr {
                            td {
                                a(href:method_href(comment[1])) {
                                    span(comment[1])
                                }
                            }
                            td {
                                span(comment[2])
                            }
                        }
                    }
                }
            }
        }
    }
}

String method_frame(String method_id) {
    "/method_frame/" + method_id
}

String method_href(String method_id) {
    "/method/" + method_id
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
    }
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
    }
}

/**
 * Workaround for the fact that StreamingMarkupBuilder can't handle mixed text and element content
 * while MarkupBuilder can.
 * @param cl
 * @return
 */
def markup(Closure cl)
{
    def sw = new StringWriter(1000)

    cl.delegate = new MarkupBuilder(sw)

    cl.call()

    sw.toString()
}