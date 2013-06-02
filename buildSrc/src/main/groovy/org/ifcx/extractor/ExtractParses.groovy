package org.ifcx.extractor

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.ifcx.extractor.util.Sexp

import java.util.zip.GZIPInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ExtractParses extends SourceTask {
    @OutputDirectory
    File parses

    @TaskAction
    public void process()
    {
        println "Extracting parses..."

        parses.mkdirs()

        source.each { f ->
            if (f.name.endsWith('.gz')) {
                f.withInputStream { gzip_in ->
                    new GZIPInputStream(gzip_in).withReader { process(new BufferedReader(it)) }
                }
            } else {
                f.withReader { process(new BufferedReader(it)) }
            }
        }
    }

    void process(BufferedReader reader)
    {
        def sentence_pat = ~/^\[(\d+)\]\s*\((\d+) of (\d+)\)\s*\{\d+\}\s*`(.*)'$/
        def parse_pat = ~/^\[(\d+):(\d+)\]\s*\(((:?in)?)active\)\s*$/

        def parse_num

        ZipOutputStream zip_os

        def eol_bytes = "\n".bytes

        String line
        while ((line = reader.readLine()) != null) {
            def sentence_matcher = sentence_pat.matcher(line)
            if (sentence_matcher.matches()) {
                def (_, sent_num, n, tot, sent) = sentence_matcher[0]

                if (zip_os != null) {
                    zip_os.close()
                }

                File sentence_zip_file = new File(parses, String.sprintf("sentence_%05d.zip", sent_num as Integer))
                FileOutputStream file_os = new FileOutputStream(sentence_zip_file)
                zip_os = new ZipOutputStream(file_os)

                def sent_info = ["SENTENCE", ["Number", sent_num], ["ParseCount", tot], ["Sentence", sent]]

                zip_os.putNextEntry(new ZipEntry("_info.txt"))
                zip_os.write(Sexp.printTree(sent_info).bytes)
            } else {
                def parse_matcher = parse_pat.matcher(line)
                if (parse_matcher.matches()) {
                    def (_, sent_num, _parse_num) = parse_matcher[0]

                    parse_num = _parse_num
                } else if (line.startsWith("(")) {
                    zip_os.putNextEntry(new ZipEntry(String.sprintf("phrase_%04d.txt", parse_num as Integer)))
                    while (line) {
                        zip_os.write(line.bytes)
                        zip_os.write(eol_bytes)
                        line = reader.readLine()
                    }
                } else if (line.startsWith("<dmrs")) {
                    zip_os.putNextEntry(new ZipEntry(String.sprintf("dmrs_%04d.xml", parse_num as Integer)))
                    while (line) {
                        zip_os.write(line.bytes)
                        zip_os.write(eol_bytes)
                        line = reader.readLine()
                    }
                }
            }
        }

        if (zip_os !=null ) zip_os.close()
    }

}
