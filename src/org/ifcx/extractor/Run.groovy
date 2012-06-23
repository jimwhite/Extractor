package org.ifcx.extractor

import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager
import javax.tools.JavaFileObject
import com.sun.tools.javac.api.JavacTool
import groovy.xml.MarkupBuilder
import com.sun.tools.javac.util.Context
import com.sun.tools.javadoc.Messager
import com.sun.tools.javadoc.JavadocClassReader
import com.sun.tools.javadoc.JavadocEnter
import com.sun.tools.javadoc.JavadocTool

JavaCompiler compiler = new JavacTool() // ToolProvider.getSystemJavaCompiler();

StandardJavaFileManager fileman = compiler.getStandardFileManager(null, null, null);

def jar_files = [/*new File('out/production/Extractor')*/]
new File("lib").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
new File("/Users/jim/Projects/Apache/Ant/apache-ant-1.8.4/lib/optional").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
String classpath = jar_files.path.join(File.pathSeparator)
//println classpath
println "${jar_files.size()} jar files."
assert fileman.handleOption("-classpath", [classpath].iterator())

RDFExtractor.rdfPath.get(0).mkdirs();
fileman.setLocation(RDFExtractor.rdfLocation, RDFExtractor.rdfPath);

def files = [:]
[new File('src'), new File('jdksrc/src'), new File('/Users/jim/Projects/Apache/Ant/apache-ant-1.8.4/src/main')].each { File dir ->
    dir.eachFileRecurse { if ((it.name ==~ /.*.java$/)) files[it.path.substring(dir.path.length())] = it }
}

//println files
println "${files.size()} java files"

Iterable<? extends JavaFileObject> units = fileman.getJavaFileObjectsFromFiles(files.values());

JavaCompiler.CompilationTask task = compiler.getTask(null, // out
        fileman, // fileManager
        null, // diagnosticsListener
        ["-d", "tmp"], // options
//                List.of("-printsource"), // options
        null, // classes
        units);

Context context = task.context

Messager.preRegister(context, "JavacGrep")
//JavadocEnter.preRegister(context)

JavadocTool javadoc = JavadocTool.make0(context)

JavadocClassReader.instance0(context)

new File("tmp/run-output.html").withPrintWriter {
//    def framer = new JavacFramer(it)
    def html = new MarkupBuilder(it)

    html.html {
        def processor = new JavacGrepHTML(javadoc.docenv, html)
        task.setProcessors(com.sun.tools.javac.util.List.of(processor));
        task.call()
    }
}
