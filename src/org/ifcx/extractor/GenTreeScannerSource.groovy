package org.ifcx.extractor

import com.sun.source.tree.TreeVisitor
import java.lang.reflect.Method
import org.ifcx.extractor.util.RDFaWriter
import com.sun.source.tree.Tree
import com.sun.source.tree.CompilationUnitTree

className = "GenTreeScanner"
returnType = Object.class
parameterType = RDFaWriter.class

new File(new File('gen'), className + ".java").withPrintWriter { writer ->
    def treeListType = CompilationUnitTree.class.declaredMethods.find { it.name == 'getTypeDecls' }.returnType

    writer.println "package org.ifcx.extractor.gen;\n"
    writer.println "public class $className extends ${TreeVisitor.class.name}<${returnType.name}, ${parameterType.name}>"
    writer.println "{\n"

    def methods = TreeVisitor.class.declaredMethods
    methods.each { Method method ->
        println method
        def visitType = method.parameterTypes[0]
        writer.println "public ${returnType.name} ${method.name}(${visitType.name}, ${parameterType.name})"
        writer.println "{"
        visitType.declaredMethods.sort{ it.name }.each { writer.println "// ${Tree.isAssignableFrom(it.returnType)} ${treeListType.isAssignableFrom(it.returnType)} ${it}" }
        writer.println "   return null;"
        writer.println "}\n"
    }

    writer.println "}"
}
