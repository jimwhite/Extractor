package org.ifcx.extractor

import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.Tree
import com.sun.source.tree.TreeVisitor
import com.sun.tools.javac.api.JavacTool

import java.lang.reflect.Method
import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager
import javax.tools.StandardLocation

className = "SexpTreeScanner"
returnType = Object.class
parameterType = IndentPrinter.class

new File(new File('gen'), className + ".java").withPrintWriter { writer ->
    def treeListType = CompilationUnitTree.class.declaredMethods.find { it.name == 'getTypeDecls' }.returnType

    writer.println "package org.ifcx.extractor.gen;\n"
    writer.println "public class $className extends org.ifcx.extractor.TreeScanner<${returnType.name}, ${parameterType.name}>"
    writer.println "{\n"

    def methods = TreeVisitor.class.declaredMethods
    methods.each { Method method ->
        def visitType = method.parameterTypes[0]
        writer.println "public ${returnType.name} ${method.name}(${visitType.name} tree, ${parameterType.name} param)"
        writer.println "{ "
        def vertexName = visitType.name.substring(visitType.package.name.length() + 1) - ~/Tree$/ ?: 'Other'
        writer.println "   param.println(\"(${vertexName}\");"
        writer.println "   param.incrementIndent();"
        visitType.declaredMethods.sort{ it.name }.each { propertyMethod ->
            def methodName = propertyMethod.name
            writer.println "// ${Tree.isAssignableFrom(propertyMethod.returnType)} ${treeListType.isAssignableFrom(propertyMethod.returnType)} ${propertyMethod} ${propertyMethod.returnType}"
            if (methodName.startsWith('get')) {
//                writer.println "   scan(\"${name.substring(3)}\", tree.${name}(), param);"
                writer.println "   param.println(\"(${ methodName.substring(3)}\");"
                writer.println "   param.incrementIndent();"
                writer.println "   visit${ method.returnType.name}(tree.${methodName}(), param);"
                writer.println "   param.decrementIndent()"
                writer.println "   param.println(\")\");"
            } else if ( methodName.startsWith('is')) {
                writer.println " //  scan(\"${ methodName.substring(2)}\", tree.${methodName}(), param);"
            } else {
                println "Not a getter:"
                println method
                println propertyMethod
                println()
            }
        }
        writer.println "   param.decrementIndent()"
        writer.println "   param.println(\")\");"
        writer.println "   return null;"
        writer.println "}\n"
    }

    writer.println "}"
}


JavaCompiler compiler = new JavacTool() // ToolProvider.getSystemJavaCompiler();

StandardJavaFileManager fileman = compiler.getStandardFileManager(null, null, null);

println  fileman.getLocation(StandardLocation.PLATFORM_CLASS_PATH)
