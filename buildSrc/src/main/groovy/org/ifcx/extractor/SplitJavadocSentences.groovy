package org.ifcx.extractor

import gate.Corpus
import gate.CorpusController
import gate.Factory
import gate.Gate

//@Grab(group='uk.ac.gate', module='gate-core', version='7.1')
import gate.util.persistence.PersistenceManager
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class SplitJavadocSentences extends DefaultTask {
    SplitJavadocSentences() {
        Gate.init()
    }

    @InputFile
    File gateApp

    @InputDirectory
    File inputDirectory

    @OutputDirectory
    File outputDirectory

    @TaskAction
    public void process() {
        outputDirectory.mkdirs()

        Corpus corpus = Factory.createResource("gate.corpora.CorpusImpl")

        CorpusController application = (CorpusController) PersistenceManager.loadObjectFromFile(gateApp)

        application.corpus = corpus

        inputDirectory.eachFile { source_file ->
            source_file.withReader { reader ->
                List sexp

                while ((sexp = read_one_sexp(reader)) != null) {
//                    println sexp
                    assert sexp.head() == "METHOD"
                    assert sexp.size() > 1

                    def method = sexp.tail().collectEntries()

                    def params = Factory.newFeatureMap()
//                    params.put("sourceUrl", uri.toURL() + "#" + method.Id)
                    params.put("preserveOriginalContent", true)
                    // params.put("collectRepositioningInfo", true)

                    gate.corpora.DocumentImpl doc = Factory.createResource("gate.corpora.DocumentImpl", params)

                    doc.setStringContent(method.Comment)
                    doc.init()

                    corpus.add(doc)

                    doc.name = method.Id
                }
            }
        }

        application.execute()

        corpus.size().times { docIndex ->
            def document = corpus.get(docIndex)
            def docAnnotationSet = document.getAnnotations()
            // def annotations = docAnnotationSet.get(["Token", "Sentence"] as Set<String>)
            def xml = document.toXml(docAnnotationSet)

            def sentenceSet = docAnnotationSet.get('Sentence')
            def sentenceNode = sentenceSet.firstNode()
            def sentence = document.content.getContent(sentenceNode.offset, sentenceSet.nextNode(sentenceNode).offset)

            def html = """<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${document.name}</title>
<link rel="stylesheet" href="../javadocs.css">
</head>
<body>
<div class='method-id'>${document.name}</div>
<div class='method-comment-sentence-1'>${sentence.toString()}</div>
<pre>
${xml}
</pre>
</body>
</html>
"""

            new File(outputDirectory, document.name + '.html').write(html)
        }
    }


    List read_one_sexp(Reader reader) {
        // This grammar has single quotes in token names.
//    final tokenDelimiters = "\"''()\t\r\n "
//    final tokenDelimiters = "\"()\t\r\n "
        // No quoted strings at all for these s-exprs.
        final tokenDelimiters = "()\t\r\n "

        List stack = []
        List sexps = []

        def cint = reader.read()

        loop:
        while (cint >= 0) {
            Character c = cint as Character
//        print c
            switch (c) {

                case ')':
                    // End of sexp with without beginning.
                    // Print a warning?
                    if (stack.size() < 1) break loop

                    def t = stack.pop()
                    t << sexps
                    sexps = t

                    // We read only one complete sexp.
                    if (stack.size() < 1) break loop

                    cint = reader.read()
                    break

                case '(':

                    stack.push(sexps)
                    sexps = []
                    cint = reader.read()
                    break

                case '"':
                    def str = new StringBuilder()
                    while ((cint = reader.read()) >= 0) {
                        if (cint == '"') break
                        if (cint == '\\') cint = reader.read()
                        str.append(cint as Character)
                    }
                    cint = reader.read()
                    sexps << str.toString()
                    break

                default:
                    if (c.isWhitespace() || c == 0 || c == 26 /* ASCII EOF */) {
                        cint = reader.read()
                    } else {
                        def token = new StringBuilder()
                        token.append(c)
                        while ((cint = reader.read()) >= 0) {
                            if (tokenDelimiters.indexOf(cint) >= 0) break
                            token.append(cint as Character)
                        }
                        sexps << token.toString()
                    }
            }
        }

        return sexps ? sexps[0] : null
    }

}
