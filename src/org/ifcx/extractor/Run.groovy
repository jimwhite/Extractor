package org.ifcx.extractor

import javax.tools.JavaCompiler
import javax.tools.ToolProvider
import javax.tools.StandardJavaFileManager
import javax.tools.JavaFileObject
import com.sun.tools.javac.util.List
import com.sun.tools.javac.api.JavacTool

JavaCompiler compiler = new JavacTool() // ToolProvider.getSystemJavaCompiler();

StandardJavaFileManager fileman = compiler.getStandardFileManager(null, null, null);

RDFExtractor.rdfPath.get(0).mkdirs();
fileman.setLocation(RDFExtractor.rdfLocation, RDFExtractor.rdfPath);

files = []
new File('jdksrc').eachFileRecurse { if (it.name ==~ ~/.*Object.java$/) files << it }
println files
Iterable<? extends JavaFileObject> units = fileman.getJavaFileObjectsFromFiles(files);

JavaCompiler.CompilationTask task = compiler.getTask(null, // out
        fileman, // fileManager
        null, // diagnosticsListener
        null, // options
//                List.of("-printsource"), // options
        null, // classes
        units);

task.setProcessors(List.of(new RDFExtractor()));

task.call();
