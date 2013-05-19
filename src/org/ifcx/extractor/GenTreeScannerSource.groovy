package org.ifcx.extractor

import java.lang.reflect.Method

import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.Tree
import com.sun.source.tree.TreeVisitor

import javax.tools.StandardLocation
import javax.tools.ToolProvider

className = "GenTreeScanner"
returnType = Object.class
parameterType = RDFaWriter.class

new File(new File('gen'), className + ".java").withPrintWriter { writer ->
    def treeListType = CompilationUnitTree.class.declaredMethods.find { it.name == 'getTypeDecls' }.returnType

    writer.println "package org.ifcx.extractor.gen;\n"
    writer.println "public class $className extends org.ifcx.extractor.TreeScanner<${returnType.name}, ${parameterType.name}>"
    writer.println "{\n"

    def methods = TreeVisitor.class.declaredMethods
    methods.each { Method method ->
        def visitType = method.parameterTypes[0]
        writer.println "public ${returnType.name} ${method.name}(${visitType.name} tree, ${parameterType.name} param)"
        writer.println "{ /*"
        def vertexName = visitType.name.substring(visitType.package.name.length() + 1) - ~/Tree$/ ?: 'Other'
        writer.println "   Vertex v = beginVertex(\"${vertexName}\", tree.getKind());"
        visitType.declaredMethods.sort{ it.name }.each { propertyMethod ->
            def name = propertyMethod.name
            writer.println "// ${Tree.isAssignableFrom(propertyMethod.returnType)} ${treeListType.isAssignableFrom(propertyMethod.returnType)} ${propertyMethod} ${propertyMethod.returnType}"
            if (name.startsWith('get')) {
                writer.println "   scan(\"${name.substring(3)}\", tree.${name}(), param);"
            } else if (name.startsWith('is')) {
                writer.println "   scan(\"${name.substring(2)}\", tree.${name}(), param);"
            } else {
                println "Not a getter:"
                println method
                println propertyMethod
                println()
            }
        }
        writer.println "   v.end();"
        writer.println "*/   return null;"
        writer.println "}\n"
    }

    writer.println "}"
}


println  ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null).getLocation(StandardLocation.PLATFORM_CLASS_PATH)
