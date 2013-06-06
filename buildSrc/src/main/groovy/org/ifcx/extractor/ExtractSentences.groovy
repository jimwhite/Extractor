package org.ifcx.extractor

import org.gradle.api.tasks.*
import org.ifcx.extractor.util.Sexp

class ExtractSentences extends SourceTask {
    @OutputFile
    File extractionIndex

    @OutputFile
    File extractedSentences

    @TaskAction
    public void process()
    {
        extractionIndex.withPrintWriter { indexPrinter ->
        extractedSentences.withPrintWriter { commentPrinter ->
            source.each { input ->
                def xml = new XmlSlurper().parse(input)
                def extracts = xml.body.'**'.findAll { it.@'class' == 'method-extract' }
                extracts.each {
                    def tree = Sexp.read_one_sexp(new StringReader(it.text()))
                    def map = Sexp.tree_to_map(tree)

                    indexPrinter.println "${map.Id}\t${input.path}"
                    commentPrinter.println map.Comment
                }
            }
        }
        }
    }

}
