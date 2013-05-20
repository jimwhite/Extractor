package org.ifcx.extractor

abstracts_file = new File("method_abstracts.txt")

method_count = 0

abstracts_file.withReader { reader ->
    def sexp

    while ((sexp = read_one_sexp(reader)) != null) {
        println sexp

        method_count += 1
    }

}

println "Read $method_count methods."

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
