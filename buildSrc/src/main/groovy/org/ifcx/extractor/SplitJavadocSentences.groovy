package org.ifcx.extractor

import gate.Corpus
import gate.CorpusController
import gate.Factory
import gate.Gate

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
    SplitJavadocSentences() {
        Gate.init()
    }

    @Input
    boolean parseCommentHTML = false

    @InputFile
    File gateApp

    @InputDirectory
    File inputDirectory

    @OutputDirectory
    File outputDirectory

    @TaskAction
    public void process()
    {
        outputDirectory.mkdirs()

        CorpusController application = (CorpusController) PersistenceManager.loadObjectFromFile(gateApp)

        inputDirectory.eachFile { source_file ->
            source_file.withReader { reader ->
                def sexp

                while ((sexp = Sexp.read_one_sexp(reader)) != null) {
                    assert sexp.size() > 1
                    assert sexp.head() == "METHOD"

                    def method = Sexp.tree_to_map(sexp)

                    processMethod(application, method)
                }
            }
        }
    }

    protected void processMethod(CorpusController application, Map method)
    {
        use (gate.Utils) {

            Corpus corpus = Factory.createResource("gate.corpora.CorpusImpl")

            application.corpus = corpus

            if (parseCommentHTML) {
//                def htmlFile = new File(outputDirectory, method.Id + '.html')
                def tempDir = new File("tmp/comments")
                tempDir.mkdirs()
                def htmlFile = File.createTempFile("mx-", ".html", tempDir)

                htmlFile.write("<html><body>" + method.Comment + "</body></html")

                def params = Factory.newFeatureMap()
                params.put("preserveOriginalContent", true)

                def doc = gate.Factory.newDocument(htmlFile.toURL())
                doc.name = method.Id
                corpus.add(doc)
            } else {
                def comment = StringEscapeUtils.unescapeHtml(method.Comment)
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
                method.Sentence = document.cleanStringFor(sentenceList.first())
                method.SentenceAll = sentenceList.collect { document.cleanStringFor(it) }
            }

            new File(outputDirectory, document.name + '.html').withPrintWriter { printer ->
                def builder = new MarkupBuilder(printer)
                builder.html(lang:"en", xmlns:"http://www.w3.org/1999/xhtml", 'xmlns:gate':"urn:gate:fakeNS") {
                    head {
                        title(document.name)
                        link(rel:"stylesheet", href:"../javadocs.css")
                    }
                    body {
                        div('class':'method', id:document.name) {
                            div('class':'method-id', document.name)
                            div('class':'method-comment-sentence-1', method.Sentence)
                            div('class':'method-sentences') {
                                method.SentenceAll.each { sent -> div('class':'method-comment-sentence', sent) }
                            }
                        }
                        pre('class':'method-comment-gate') {
                            mkp.yieldUnescaped(xml)
                        }
                        pre('class':'method-extract', Sexp.printTree(Sexp.map_to_tree(method)))
                    }
                }
            }

            /*
            def html = """<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:gate="urn:gate:fakeNS">
<head>
<title>${document.name}</title>
<link rel="stylesheet" href="../javadocs.css"/>
</head>
<body>
<div class='method' id='${document.name}'>
  <div class='method-id'>${document.name}</div>
  <div class='method-comment-sentence-1'>${method.Sentence}</div>
<div class='method-sentences'>
${method.SentenceAll.collect { sent -> "<div class='method-comment-sentence'>$sent</div>" }.join('\n')}
</div>
<pre class='method-comment-gate'>
${xml}
</pre>
<pre class='method-extract'>
${Sexp.printTree(Sexp.map_to_tree(method))}
</pre>
</div>
</body>
</html>
"""
*/

        }

    }

}
