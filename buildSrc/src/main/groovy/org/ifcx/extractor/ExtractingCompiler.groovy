package org.ifcx.extractor

import com.sun.tools.javac.api.JavacTool
import com.sun.tools.javac.util.Context
import com.sun.tools.javadoc.JavadocClassReader
import com.sun.tools.javadoc.JavadocTool
import com.sun.tools.javadoc.Messager
import groovy.xml.MarkupBuilder
import org.gradle.api.internal.tasks.compile.*;
import org.gradle.api.internal.tasks.compile.Compiler;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.compile.CompileOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

public class ExtractingCompiler implements Compiler<ExtractJavadocSpec>, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractingCompiler.class);

    public WorkResult execute(ExtractJavadocSpec spec) {
        LOGGER.info("Compiling with Javadoc Extractor.");
//        println "Compiling with Javadoc Extractor."

        JavaCompiler.CompilationTask task = createCompileTask(spec);

        Context context = task.context

        Messager.preRegister(context, "JavadocGrep")

        JavadocTool javadoc = JavadocTool.make0(context)
        JavadocClassReader.instance0(context)

        boolean success = false

        spec.htmlReport.withPrintWriter {
            def html = new MarkupBuilder(it)

            html.html {
                head {

                }
                body {
                    def processor = new JavadocGrepHTML(context, html, spec.methodAbstracts)
                    task.setProcessors(com.sun.tools.javac.util.List.of(processor));
                    success = task.call()
                }
            }
        }

        if (!success) {
            throw new CompilationFailedException();
        }

        return new SimpleWorkResult(true);
    }

    private JavaCompiler.CompilationTask createCompileTask(ExtractJavadocSpec spec) {
        List<String> options = new JavaCompilerArgumentsBuilder(spec).build();
        JavaCompiler compiler = new JavacTool();

        CompileOptions compileOptions = spec.getCompileOptions();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, compileOptions.getEncoding() != null ? Charset.forName(compileOptions.getEncoding()) : null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(spec.getSource());
//        compilationUnits.each { println it }
        return compiler.getTask(null, null, null, options, null, compilationUnits);
    }
}
