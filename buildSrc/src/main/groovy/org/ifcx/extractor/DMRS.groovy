package org.ifcx.extractor

import groovy.util.slurpersupport.Node

import org.ifcx.extractor.util.Sexp

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

            top_nodeid_candidates.remove(to_nodeid)

            if (post == 'EQ') {
                top_nodeid_candidates.remove(from_nodeid)
                nodes[to_nodeid].add(['EQ', ['-' + rargname, ['post', post], nodes[from_nodeid]]])
            } else {
                nodes[from_nodeid].add([rargname, ['post', post], nodes[to_nodeid]])
            }
        }

        if (top_nodeid_candidates.size() > 1) {
            top_nodeid_candidates.each { topid ->
                println topid
                println Sexp.printTree(nodes[topid])
            }
        }

        assert top_nodeid_candidates.size() == 1

        nodes[top_nodeid_candidates[0]]
    }

    static void main(String[] args)
    {
        def dmrs = parseXML(new File(args[0]))

//        println Sexp.printTree(["DMRS"] + dmrs.values())
        println Sexp.printTree(dmrs)
    }
}
