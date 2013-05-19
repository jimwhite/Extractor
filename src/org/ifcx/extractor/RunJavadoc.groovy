package org.ifcx.extractor

import com.sun.tools.javac.api.JavacTool
import com.sun.tools.javac.util.Context
import com.sun.tools.javadoc.JavadocClassReader
import com.sun.tools.javadoc.JavadocTool
import com.sun.tools.javadoc.Messager
import groovy.xml.MarkupBuilder

import javax.tools.JavaCompiler
import javax.tools.JavaFileObject
import javax.tools.StandardJavaFileManager

JavaCompiler compiler = new JavacTool() // ToolProvider.getSystemJavaCompiler();

StandardJavaFileManager fileman = compiler.getStandardFileManager(null, null, null);

def jar_files = [/*new File('out/production/Extractor')*/]
new File("lib").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
new File("/Users/jim/Projects/Apache/Ant/apache-ant-1.8.4/lib/optional").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
new File("/Users/jim/Downloads/stanford-corenlp-full-2012-11-12/").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
new File("/Users/jim/Projects/Apache/Lucene/lucene-3.5.0/lib").eachFile { if (it.isFile() && it.name.endsWith('.jar')) jar_files += it }
String classpath = jar_files.path.join(File.pathSeparator)
//println classpath
println "${jar_files.size()} jar files."
assert fileman.handleOption("-classpath", [classpath].iterator())

//RDFExtractor.rdfPath.get(0).mkdirs();
//fileman.setLocation(RDFExtractor.rdfLocation, RDFExtractor.rdfPath);

def files = [:]
[new File('timesrc') , new File('/Users/jim/Projects/Apache/Lucene/lucene-3.5.0/src/java') , new File('/Users/jim/Downloads/stanford-corenlp-full-2012-11-12/src') , new File('src'),  new File('jdksrc/src'), new File('/Users/jim/Projects/Apache/Ant/apache-ant-1.8.4/src/main')].each { File dir ->
    dir.eachFileRecurse { if ((it.name ==~ /.*.java$/) && !(it.path =~ "org/ifcx")) files[it.path.substring(dir.path.length())] = it }
}

//println files
println "${files.size()} java files"

Iterable<? extends JavaFileObject> units = fileman.getJavaFileObjectsFromFiles(files.values());

JavaCompiler.CompilationTask task = compiler.getTask(null, // out
        fileman, // fileManager
        null, // diagnosticsListener
        ["-d", "tmp", "-printsource"], // options
//                List.of("-printsource"), // options
        null, // classes
        units);

Context context = task.context

//com.sun.tools.apt.main.JavaCompiler.instance(context)

Messager.preRegister(context, "JavadocGrep")
//JavadocEnter.preRegister(context)
//
JavadocTool javadoc = JavadocTool.make0(context)
//
JavadocClassReader.instance0(context)

new File("tmp/doc-output.html").withPrintWriter {
//    def framer = new JavacFramer(it)
    def html = new MarkupBuilder(it)

    html.html {
        head {

        }
        body {
            def processor = new JavadocGrepHTML(context, html)
            task.setProcessors(com.sun.tools.javac.util.List.of(processor));
            task.call()
        }
    }
}
