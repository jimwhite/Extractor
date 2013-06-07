package org.ifcx.extractor

import gate.Corpus
import gate.CorpusController
import gate.Document
import gate.Factory
import gate.Gate
import gate.corpora.DocumentImpl
// Using the gate.Utils category doesn't make the static featureMap(Object...) accessible.
import static gate.Utils.featureMap

//@Grab(group='uk.ac.gate', module='gate-core', version='7.1')
import gate.util.persistence.PersistenceManager
import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringEscapeUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.ifcx.extractor.util.Sexp

class SplitJavadocSentences extends DefaultTask {
    @Input
    boolean parseCommentHTML = false

    @InputFile
    File gateApp

    @InputDirectory
    File inputDirectory

    @InputDirectory
    File updatesDirectory

    @OutputDirectory
    File outputDirectory

    static initialized = false

    @TaskAction
    public void process()
    {
        synchronized (SplitJavadocSentences.class) {
            if (!initialized) {
                initialized = true
                Gate.init()
            }
        }

        outputDirectory.mkdirs()

        CorpusController application = (CorpusController) PersistenceManager.loadObjectFromFile(gateApp)

        inputDirectory.eachFile { source_file ->
            source_file.withReader { reader ->
                def sexp

                while ((sexp = Sexp.read_one_sexp(reader)) != null) {
                    assert sexp.size() > 1
                    assert sexp.head() == "METHOD"

                    def method = Sexp.tree_to_map(sexp)

                    def updateFile = new File(updatesDirectory, method.Id + ".txt")
                    if (updateFile.exists()) {
                        updateFile.withReader {
                            def method_update

                            while ((method_update = Sexp.read_one_sexp(it)) != null) {
                                assert method_update.head() == "METHOD_UPDATE"
                                method_update.tail().each { method[it[0]] = it[1] }
                            }
                        }
                    }

                    processMethod(application, method)
                }
            }
        }
    }

    protected void processMethod(CorpusController application, Map method)
    {
        use (gate.Utils) {

            Corpus corpus = Factory.createResource(gate.corpora.CorpusImpl.class.name)

            application.corpus = corpus

            if (parseCommentHTML) {
//                def htmlFile = new File(outputDirectory, method.Id + '.html')
                def tempDir = new File("tmp/comments")
                tempDir.mkdirs()
                def htmlFile = File.createTempFile("mx-", ".html", tempDir)

                htmlFile.write("<html><body>" + method.Javadoc + "</body></html")

//                def doc = gate.Factory.newDocument(htmlFile.toURL())
                def params = featureMap(Document.DOCUMENT_URL_PARAMETER_NAME, htmlFile.toURL()
                        , Document.DOCUMENT_PRESERVE_CONTENT_PARAMETER_NAME, true)
                def doc = Factory.createResource(DocumentImpl.class.name, params)

                doc.name = method.Id
                corpus.add(doc)
            } else {
                def comment = StringEscapeUtils.unescapeHtml(method.Javadoc)
                def doc = Factory.newDocument(comment)
                doc.name = method.Id
                doc.preserveOriginalContent = true
                corpus.add(doc)
            }

            application.execute()

            def document = corpus.get(0)

            def docAnnotationSet = document.getAnnotations()
            // def annotations = docAnnotationSet.get(["Token", "Sentence"] as Set<String>)
            def xml = document.toXml(docAnnotationSet)

            def sentenceSet = docAnnotationSet.get('Sentence')
            def sentenceList = sentenceSet.inDocumentOrder()

            if (sentenceList.size() > 0) {
                method.Sentences = sentenceList.collect { document.cleanStringFor(it) }
                def comment = tidyJavadoc(method.Sentences.first())
                if (comment) method.Comment0 = comment
            }

            if (!method.Comment && method.Comment0) method.Comment = method.Comment0

//            writeMethod(document, xml, method)
            MethodData.writeMethod(outputDirectory, document.name, method)
        }
    }

/*
    private void writeMethod(Document document, xml, Map method) {
        new File(outputDirectory, document.name + '.html').withPrintWriter { printer ->
            new MarkupBuilder(printer).html(lang: "en", xmlns: "http://www.w3.org/1999/xhtml", 'xmlns:gate': "urn:gate:fakeNS") {
                head {
                    title(document.name)
                    link(rel: "stylesheet", href: "../javadocs.css")
                }
                body {
                    form('class': 'method', id: document.name, action: "update-method/${document.name}", method: 'post') {
                        div('class': 'method-id', document.name)
                        div {
                            def radio = {
                                if ((method.Judgement ?: 'Unknown') == it)
                                    input(type: 'radio', name: 'Judgement', value: it, checked: true, it)
                                else
                                    input(type: 'radio', name: 'Judgement', value: it, it)
                            }
                            radio('Unknown')
                            radio('Pedantic')
                            radio('SomewhatPedantic')
                            radio('NotPedantic')
                            input(type: 'submit')
                        }
                        if (method.Comment)
                            input('class': 'method-comment', type: 'text', name: 'Comment', value: method.Comment)
                        else
                            p("No comment")
                        if (method.Comment0) div('class': 'method-comment-original', method.Comment0)
                        if (method.Sentences) {
                            ol('class': 'method-sentences') {
                                method.Sentences.each { sent -> li('class': 'method-sentence', sent) }
                            }
                        }
                    }
                    pre('class': 'method-comment-gate') {
                        mkp.yieldUnescaped(xml)
                    }
                    pre('class': 'method-extract', Sexp.printTree(Sexp.map_to_tree(method)))
                }
            }
        }
    }
*/

    static String tidyJavadoc(String sentence)
    {
        sentence.replaceAll(/\{\s*@[^{}]*\}/, '').trim()
    }
}
