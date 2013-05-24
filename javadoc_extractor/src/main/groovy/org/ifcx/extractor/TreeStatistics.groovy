package org.ifcx.extractor

import distance.RTED_InfoTree_Opt
import util.LblTree

abstracts_file = new File("method_abstracts.txt")

method_count = 0

Map<Integer, LblTree> red_trees = [:]

def empty_tree = new LblTree("ROOT", 0)

abstracts_file.withReader { reader ->
    def sexp

    while ((sexp = read_one_sexp(reader)) != null) {
        method_count += 1

        assert sexp.head() == "METHOD"

        def method = sexp.tail().collectEntries()

        def red_tree = sexp_to_red_tree(method.Tree)

        def root = new LblTree("ROOT", method_count)
        root.add(red_tree)

        red_trees[method_count] = root

/*
        def insertion_cost = rted.nonNormalizedTreeDist(empty_tree, root)
        def deletion_cost = rted.nonNormalizedTreeDist(root, empty_tree)

        if (insertion_cost != deletion_cost || insertion_cost != red_tree.nodeCount) {
            println sexp
            println method
            println root
            println insertion_cost
            println deletion_cost
            println()
        }
*/
    }
}

println "Read $method_count methods."

scorer = new Scorer()

tree1 = red_trees[1]

higest_f_score = 0

red_trees.size().times { i ->
    def tree_i = red_trees[i + 1]
/*
    def dist_1_to_i = rted.nonNormalizedTreeDist(tree1, tree_i)
    def edits_1_to_i = rted.computeEditMapping()
    def insertions = 0
    def deletions = 0
    edits_1_to_i.each { m ->
        if (m[0] == 0) insertions += 1
        if (m[1] == 0) deletions += 1
    }
    def target_size = tree_i.nodeCount - 1
    def source_size = tree1.nodeCount - 1
    def precision = (target_size - insertions) / target_size
    def recall = (source_size - deletions) / source_size
    def f_score = 2*(precision*recall)/(precision+recall)
*/

    def score = scorer.score_trees(tree1, tree_i)

    if (score.f > higest_f_score) {
        println tree1
        println tree_i
        println "${score.source_size} ${score.target_size} ${score.insertions} ${score.deletions} ${score.distance} P:${score.precision} R:${score.recall} F:${score.f}"
        println()
        if (score.f < 1) higest_f_score = score.f
    }
}

all_avg_f_scores = red_trees.keySet().collect { i ->
    def tree_i = red_trees[i]


    def other_keys = red_trees.keySet() as List
    other_keys.remove((Object) i)
    def scores = other_keys.collect { j ->
        def tree_j = red_trees[j]
        scorer.score_trees(tree_i, tree_j)
    }

    def total_f = scores.f.sum()

    def avg_f_score = total_f / scores.size()

    println "${tree_i.treeID}: ${avg_f_score}"

    avg_f_score
}

total_avg_f_scores = all_avg_f_scores.sum()

println "Average of all average f scores ${total_avg_f_scores/all_avg_f_scores.size()}"

class Scorer {

    RTED_InfoTree_Opt rted = new RTED_InfoTree_Opt(1, 1, 1e8);

    def score_trees(LblTree gold_tree, LblTree sys_tree)
    {
        def dist_1_to_i = rted.nonNormalizedTreeDist(gold_tree, sys_tree)
        def edits_1_to_i = rted.computeEditMapping()
        def insertions = 0
        def deletions = 0
        edits_1_to_i.each { m ->
            if (m[0] == 0) insertions += 1
            if (m[1] == 0) deletions += 1
        }
        def source_size = gold_tree.nodeCount - 1
        def target_size = sys_tree.nodeCount - 1
        def recall = (source_size - deletions) / source_size
        def precision = (target_size - insertions) / target_size
        def f_score = 2*(precision*recall)/(precision+recall)
        [source_size:source_size, target_size:target_size, insertions:insertions, deletions:deletions, distance:dist_1_to_i, precision:precision, recall:recall, f:f_score]
    }
}

LblTree sexp_to_red_tree(sexp)
{
    if (sexp instanceof List) {
        LblTree node = null

        if (sexp.size()) {
            def node_label = sexp.head() as String
            node = new LblTree(node_label, -1)

            if (node_label != "IDENTIFIER" ) {
                sexp.tail().each {
                    def child = sexp_to_red_tree(it)
                    if (child != null) node.add(child)
                }
            }
        }

        node
    } else {
        new LblTree(sexp as String, -1)
    }
}

def read_one_sexp(Reader reader)
{
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

            case ')' :
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
                if (c.isWhitespace() || c == 0 || c == 26 /* ASCII EOF */ ) {
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
