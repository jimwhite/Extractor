package org.ifcx.extractor

import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager
import javax.tools.JavaFileObject
import com.sun.tools.javac.api.JavacTool
import groovy.xml.MarkupBuilder

JavaCompiler compiler = new JavacTool() // ToolProvider.getSystemJavaCompiler();

StandardJavaFileManager fileman = compiler.getStandardFileManager(null, null, null);

def jar_files = [/*new File('out/production/Extractor')*/]
new File("lib").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
String classpath = jar_files.path.join(File.pathSeparator)
//println classpath
println "${jar_files.size()} jar files."
assert fileman.handleOption("-classpath", [classpath].iterator())

RDFExtractor.rdfPath.get(0).mkdirs();
fileman.setLocation(RDFExtractor.rdfLocation, RDFExtractor.rdfPath);

def files = [:]
[new File('src'), new File('jdksrc/src')].each { File dir ->
    dir.eachFileRecurse { if ((it.name ==~ /.*.java$/)) files[it.path.substring(dir.path.length())] = it }
}

//println files
println "${files.size()} java files"

Iterable<? extends JavaFileObject> units = fileman.getJavaFileObjectsFromFiles(files.values());

JavaCompiler.CompilationTask task = compiler.getTask(null, // out
        fileman, // fileManager
        null, // diagnosticsListener
        null,
//        ["-classpath", classpath], // options
//                List.of("-printsource"), // options
        null, // classes
        units);

new File("run-output.html").withPrintWriter {
//    def framer = new JavacFramer(it)
    def html = new MarkupBuilder(it)

    html.html {
        def processor = new JavacGrepHTML(html)
        task.setProcessors(com.sun.tools.javac.util.List.of(processor));
        task.call()
    }
}
