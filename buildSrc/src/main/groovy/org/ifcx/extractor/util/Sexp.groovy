package org.ifcx.extractor.util

class Sexp
{
    static String SEXP_LABEL_KEY = "_sexp_label"

    static def printTree(Object tree)
    {
        def sw = new StringWriter()
        sw.withPrintWriter { printTree(tree, new IndentWriter(it) ) }
        sw.toString()
    }

    static def printTree(Object tree, IndentWriter writer)
    {
        if (tree instanceof List) {
            writer.print "("
            def indent = writer + 1
            if (!tree.isEmpty()) {
                def head = tree.head()
                if (head instanceof List) {
                    printTree(head, indent)
                } else {
                    writer.print sexpString(head.toString())
                }
                def tail = tree.tail()
                tail.each { if (tail.size() > 1) indent.println() ; printTree(it, indent) }
            }
            indent.print ")"
        } else {
            writer.print " " + sexpString(tree.toString())
//        writer.print " '$tree'"
        }
    }

    static def sexpString(String str)
    {
        str = str.replace('\\', '\\\\').replace("(", "\\(").replace(")", "\\)").replace("\"", "\\\"")
        (!str || str.contains(" ") || str.contains("\\")) ? "\"" + str + "\"" : str
    }

    static def read_one_sexp(Reader reader)
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

    static def tree_to_map(Object tree)
    {
        if (tree instanceof List) {
            def sexp_label = tree.head()
            def amap = tree.tail().collectEntries()
            amap[SEXP_LABEL_KEY] = sexp_label
            tree = amap
        }

        tree
    }

    static def map_to_tree(Map amap)
    {
        [amap[SEXP_LABEL_KEY]] + (amap.keySet().sort() - SEXP_LABEL_KEY).collect { [it, amap[it]] }
    }
}
