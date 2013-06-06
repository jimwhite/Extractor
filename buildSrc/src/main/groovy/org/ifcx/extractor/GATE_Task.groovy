package org.ifcx.extractor

//@Grab(group='uk.ac.gate', module='gate-core', version='7.1')

import gate.Corpus
import gate.CorpusController
import gate.Document
import gate.Factory
import gate.Gate
import gate.util.persistence.PersistenceManager
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class GATE_Task extends SourceTask
{
    static initialized = false

    @InputFile
    File gateApp

    @OutputDirectory
    File outputDirectory

    @TaskAction
    public void process()
    {
//        println "GATE_Task processing..."

        synchronized (GATE_Task.class) {
            if (!initialized) {
                initialized = true
                Gate.init()
            }
        }

        outputDirectory.mkdirs()

        Corpus corpus = Factory.createResource(gate.corpora.CorpusImpl.class.name)

        CorpusController application = (CorpusController)PersistenceManager.loadObjectFromFile(gateApp)

        application.corpus = corpus

        source.each { File sourceFile ->
            def uri = sourceFile.toURI()
            def params = Factory.newFeatureMap()
            params.put("sourceUrl", uri.toURL())
            params.put("preserveOriginalContent", true)
            // params.put("collectRepositioningInfo", true)

            Document doc = (Document) Factory.createResource(gate.corpora.DocumentImpl.class.name, params)

            corpus.add(doc)

            doc.name = uri.toString()
        }

        application.execute()

        corpus.size().times { docIndex ->
            def document = corpus.get(docIndex)
            def docAnnotationSet = document.getAnnotations()
            // def annotations = docAnnotationSet.get(["Token", "Sentence"] as Set<String>)
            def xml = document.toXml(docAnnotationSet)

            def html = """<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Processed</title>
</head>
<style type="text/css">
BODY, body { margin: 2em } /* or any other first level tag */
P, p { display: block } /* or any other paragraph tag */
/* ANNIE tags but you can use whatever tags you want */
/* be careful that XML tags are case sensitive */
Sentence  { background-color: rgb(150, 230, 150) ; border-spacing:1px 1px; border-style: none solid none solid }
Token     { background-color: rgb(230, 150, 230) ; border: 1px solid red /* border-spacing:1px 1px; border-style: none dotted none dotted */ }
</style>
<body>
<pre>
${xml}
</pre>
</body>
</html>
"""

            new File(outputDirectory, document.name.replaceAll(/\W+/, '_') + '.html').write(html)
        }
    }

}
