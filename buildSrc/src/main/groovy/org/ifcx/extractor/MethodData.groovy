package org.ifcx.extractor

import groovy.xml.MarkupBuilder
import org.ifcx.extractor.util.Sexp

class MethodData
{
    static def writeMethod(File outputDirectory, String method_id, Map method)
    {
        new File(outputDirectory, method_id + '.html').withPrintWriter { printer ->
            new MarkupBuilder(printer).html(lang:"en", xmlns:"http://www.w3.org/1999/xhtml", 'xmlns:gate':"urn:gate:fakeNS") {
                head {
                    title(method_id)
                    link(rel:"stylesheet", href:"../javadocs.css")
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
                            radio1('Unknown')
                            radio1('S')
                            radio1('VP')
                            radio1('NP')
                        }
                        div {
                            def radio = {
                                if ((method.Judgement ?: 'Unknown') == it)
                                    input(type:'radio', name:'Judgement', value:it, checked:true, it)
                                else
                                    input(type:'radio', name:'Judgement', value:it, it)
                            }
                            radio('Unknown')
                            radio('Pedantic')
                            radio('SomewhatPedantic')
                            radio('NotPedantic')
                            input(type:'submit')
                        }

                        if (method.Comment)
                            input('class':'method-comment', type:'text', name:'Comment', value:method.Comment)
                        else
                            p("No comment")

                        input('class':'method-note', type:'text', name:'Note', value:method.Note)

                        pre(method.JavaSource)

                        if (method.Comment0) div('class':'method-comment-original', method.Comment0)
                        if (method.Sentences) {
                            ol('class':'method-sentences') {
                                method.Sentences.each { sent -> li('class':'method-sentence', sent) }
                            }
                        }
                    }
                    pre('class':'method-extract', Sexp.printTree(Sexp.map_to_tree(method)))
                }
            }
        }
    }

    static Map readMethod(File directory, String method_id)
    {
        readMethod(new File(directory, method_id + ".html"))
    }

    static Map readMethod(File method_file)
    {
        def xml = new XmlSlurper().parse(method_file)
        def extracts = xml.body.'**'.findAll { it.@'class' == 'method-extract' }

        if (extracts.size() > 0) {
            if (extracts.size() > 1) println "More method extracts than expected: ${extracts.size()} for $method_file"
            def tree = Sexp.read_one_sexp(new StringReader(extracts[0].text()))
            Sexp.tree_to_map(tree)
        } else {
            println "No method extracts for $method_file"
            null
        }
    }
}
