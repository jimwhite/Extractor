package org.ifcx.extractor

import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager
import javax.tools.JavaFileObject
import com.sun.tools.javac.api.JavacTool

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

files = []
def source_dir = new File('src')  // new File('jdksrc')
//source_dir.eachFileRecurse { if ((it.name ==~ /.*.java$/) && !it.path.contains('tinkerpop')  && !it.path.contains('ifcx')) files << it }
source_dir.eachFileRecurse { if ((it.name ==~ /.*.java$/)) files << it }
//println files
println "${files.size()} java files"

Iterable<? extends JavaFileObject> units = fileman.getJavaFileObjectsFromFiles(files);

JavaCompiler.CompilationTask task = compiler.getTask(null, // out
        fileman, // fileManager
        null, // diagnosticsListener
        null,
//        ["-classpath", classpath], // options
//                List.of("-printsource"), // options
        null, // classes
        units);

task.setProcessors(com.sun.tools.javac.util.List.of(new JavacFramer()));

new File("run-output.txt").withOutputStream {
    System.setOut(new PrintStream(it))
    task.call()
    System.out.flush()
}
