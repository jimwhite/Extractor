package org.ifcx.extractor

import groovy.util.slurpersupport.Node

import org.ifcx.extractor.util.Sexp

import java.util.zip.ZipFile

class DMRS
{
    static def parseXML(input)
    {
        def dmrs = new XmlSlurper().parse(input)

//        println dmrs.children().size()

//        dmrs.children().each { println it.name() }

        def nodes = [:]

        dmrs.node.list().each { node ->
            def sexp = ['node'/*, ['id', node.'@nodeid']*/]
            node.children().each { c ->
                def cexp = [c.name()]
                if (!(c instanceof Node) && c.text()) cexp.add(['text', c.text().toString()])
                if (c.attributes()) c.attributes().each { cexp.add([it.key, it.value.toString()]) }
                sexp.add(cexp)
            }
            if (node.attributes().containsKey('surface')) sexp.add(['surface', node.'@surface'.toString()])
            if (node.attributes().containsKey('base')) sexp.add(['base', node.'@base'.toString()])
            if (node.attributes().containsKey('carg')) sexp.add(['carg', node.'@carg'.toString()])
//            println Sexp.printTree(sexp)
            nodes[node.'@nodeid'.toString()] = sexp
        }

//        println nodes

        def top_nodeid_candidates = nodes.keySet() as List

        dmrs.link.list().each { link ->
//            nodes[node.'@nodeid'] = sexp
            def from_nodeid = link.attributes()['from'].toString()
            def to_nodeid = link.attributes()['to'].toString()
            def rargname = link.rargname.text()
            def post = link.post.text()

//            if (!(nodes.containsKey(from_nodeid))) {
//                println "Huh?!"
//            }

            if (rargname == 'RSTR') {
                def tmp_from = from_nodeid
                from_nodeid = to_nodeid
                to_nodeid = tmp_from
                rargname = '-RSTR'
            }

            if (post == 'EQ') {
                top_nodeid_candidates.remove(from_nodeid)
                nodes[to_nodeid].add(['EQ', ['-' + rargname, ['post', post], nodes[from_nodeid]]])
            } else {
                top_nodeid_candidates.remove(to_nodeid)
                nodes[from_nodeid].add([rargname, ['post', post], nodes[to_nodeid]])
            }
        }

        if (top_nodeid_candidates.size() != 1) {
            println "Didn't find exactly one top node in DMRS."
            top_nodeid_candidates.each { topid ->
                println topid
                println Sexp.printTree(nodes[topid])
            }
        }

//        assert top_nodeid_candidates.size() == 1

//        top_nodeid_candidates.size() == 1 ? nodes[top_nodeid_candidates[0]] : top_nodeid_candidates.collect { nodes[it] }

        top_nodeid_candidates.collect { nodes[it] }
    }

    static void main(String[] args)
    {
        def dmrs = parseXML(new File(args[0]))

//        println Sexp.printTree(["DMRS"] + dmrs.values())
        println Sexp.printTree(dmrs)
    }

    static def readXML(File parses_dir, Integer sent_num, Integer parse_idx = 0)
    {
        try {
            def zip_file = new ZipFile(new File(parses_dir, ExtractParses.sentence_zip_name(sent_num)))
            def zip_entry = zip_file.getEntry(ExtractParses.dmrs_file_name(parse_idx))

            def zip_entry_is = zip_file.getInputStream(zip_entry)

            zip_entry_is.text
        } catch (IOException ioe) {
            ioe.printStackTrace()
            null
        }
    }

    static def readParse(File parses_dir, Integer sent_num, Integer parse_idx = 0)
    {
        try {
            def zip_file = new ZipFile(new File(parses_dir, ExtractParses.sentence_zip_name(sent_num)))
            def zip_entry = zip_file.getEntry(ExtractParses.dmrs_file_name(parse_idx))

            def zip_entry_is = zip_file.getInputStream(zip_entry)

            parseXML(zip_entry_is)
        } catch (IOException ioe) {
            ioe.printStackTrace()
            null
        }
    }
}
