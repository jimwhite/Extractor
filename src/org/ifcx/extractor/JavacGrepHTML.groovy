package org.ifcx.extractor

import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.TreeMaker
import org.openrdf.model.vocabulary.XMLSchema

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import groovy.xml.MarkupBuilder
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import org.ifcx.extractor.util.IndentWriter
import java.lang.reflect.Method
import java.util.regex.Pattern
import com.sun.tools.javadoc.MethodDocImpl
import com.sun.tools.javadoc.DocEnv
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.LineMap
import com.sun.tools.javac.util.Context
import com.sun.javadoc.ParamTag
import com.sun.tools.javadoc.Messager
import com.sun.source.tree.LiteralTree

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavacGrepHTML extends AbstractProcessor
{
    MarkupBuilder builder
    Integer includedElements = 0

    DocEnv docEnv

    Trees trees;
    TreeMaker make;
    Elements elems;

    LineMap lineMap

    public JavacGrepHTML(DocEnv docEnv, MarkupBuilder b)
    {
        this.docEnv = docEnv

        builder = b
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        make = TreeMaker.instance(((JavacProcessingEnvironment) processingEnv).getContext());
        elems = processingEnv.getElementUtils();
        trees = Trees.instance(processingEnv);

        super.init(processingEnv);
    }

    Map<String, String> mappings = new TreeMap<String, String>() {{
        put("urn:org.ifcx.model#", "model");
        put(XMLSchema.NAMESPACE, "xsd");
    }};

    final String padding = "                                                                         ";
    final Pattern paramPattern = ~/@param\s+(\w+)\s*(.*)$/

    void printElement(int indent, Element element)
    {
        try {
            _printElement(indent, element)
        } catch (Exception e) {
//            e.printStackTrace()
            System.err.println "Exception in ${element}"
        }
    }

    void _printElement(int indent, Element element)
    {
        String docComment = elems.getDocComment(element);
        if (docComment != null && (element instanceof ExecutableElement)) {
            ExecutableElement method = (ExecutableElement) element;

            if (includeReturnArrayAccess(element)) {
//                def docLines = []
                def paramComments = [:]
                def returnComment = ''
//                docComment.eachLine { line ->
//                    line = line.trim()
//                    if (line.startsWith('@param')) {
//                        def m = paramPattern.matcher(line)
//                        if (m.find()) {
//                            paramComments[m.group(1)] = m.group(2)
//                        }
//                    } else if (line.startsWith('@return')) {
//                        returnComment = line.substring('@return'.length()).trim()
//                    } else {
//                        if (line) docLines << line
//                    }
//                }
//
//                docComment = docLines.join('\n')
//
//                if (!docComment) return ;


                def docImpl = new MethodDocImpl(docEnv, method, docComment, trees.getTree(element), lineMap)
                def methodComment = docImpl.commentText().trim()
                if (!methodComment) return ;

                def returnTags = docImpl.tags('return')
                if (returnTags.size()) returnComment = returnTags[0].text()

                docImpl.paramTags().each { ParamTag tag ->
                    paramComments[tag.parameterName()] = tag.parameterComment()
                }

                ++includedElements
                builder.div() {
//                    p {
//                        pre(docComment)
//                    }

                    div(style:'font-size:110%;') {
                        yield(methodComment, false)
                    }

                    div {
                        code(fullName(element.enclosingElement))
                    }

                    div {
                        code(method.returnType)
                        code(element.simpleName + "(" + method.parameters.simpleName.join(', ') + ")")
                        span([style:'font-size:110%;'], returnComment)
                    }

                    div {
                        for (VariableElement var : method.getParameters()) {
//                            code(var.getSimpleName() + " : " + var.asType() + " ")
                            code(var.asType())
                            code(var.simpleName)
                            span([style:'font-size:110%;'], paramComments[var.simpleName.toString()] ?: "")
                            br()
                        }
                    }

                    printMethod(indent, method);

                    hr()
                }
            }
        }

        def tree = trees.getTree(element)
        if (tree instanceof CompilationUnitTree) {
            lineMap = tree.lineMap
        }

        for (Element each : element.getEnclosedElements()) {
            printElement(indent + 2, each);
        }
    }

    static String fullName(Element element)
    {
        ((element.enclosingElement == null) ? "" : fullName(element.enclosingElement) + ".") + (element instanceof PackageElement ? element.qualifiedName : element.simpleName)
    }

    protected boolean includeReturnArrayAccess(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
            && (tree.body.statements[0].kind == Tree.Kind.RETURN )
            && (tree.body.statements[0].expression?.kind == Tree.Kind.ARRAY_ACCESS ))

    }

    protected boolean includeReturnVariable(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.RETURN )
                && (tree.body.statements[0].expression?.kind == Tree.Kind.MEMBER_SELECT ))

    }

    protected boolean includeReturnLiteral(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.RETURN )
                && (tree.body.statements[0].expression instanceof LiteralTree))

    }

    protected boolean includeSingleAssignment(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.EXPRESSION_STATEMENT )
                && (tree.body.statements[0].expression.kind == Tree.Kind.ASSIGNMENT ))

    }

    protected void printMethodPretty(indent, ExecutableElement method)
    {
        def sw = new StringWriter()
        Pretty visitor = new Pretty(sw, false);
        MethodTree tree = trees.getTree(method)
        visitor.printStat(tree.body)
//        sw.toString().eachLine { printer.println(padding.substring(0, indent) + it) }
//        printer.println "\n"
        builder.pre(sw)
    }

    protected void printMethod(indent, ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        builder.pre(tree.body)

        Object sexp = externalize(tree)

        StringWriter sw = new StringWriter()
        sw.withPrintWriter { printTree(sexp, new IndentWriter(it) ) }
        builder.pre(sw)
    }

    def externalize(Object obj)
    {
        if (obj instanceof Tree) {
            Tree tree = obj
            obj = [tree.kind]
            def treeKlazz = tree.class.interfaces.find { Tree.class.isAssignableFrom(it) }
            if (!treeKlazz) {
                return tree
            }
            treeKlazz.declaredMethods.sort{ it.name }.each { propertyMethod ->
                def name = propertyMethod.name
                // ${Tree.isAssignableFrom(propertyMethod.returnType)} ${treeListType.isAssignableFrom(propertyMethod.returnType)} ${propertyMethod} ${propertyMethod.returnType}"
                if (name.startsWith('get')) {
                    obj << [name.substring(3), externalize(propertyMethod.invoke(tree))]
                } else if (name.startsWith('is')) {
                    obj << [name.substring(2), externalize(propertyMethod.invoke(tree))]
                }
            }

        } else if (obj instanceof List) {
            obj = obj.isEmpty() ? [] : ['LIST'] + obj.collect { externalize(it) }
        } else if (obj instanceof Set) {
            obj = obj.isEmpty() ? [] : ['SET'] + obj.collect { externalize(it) }
        }

        obj
    }

    static def printTree(Object tree, IndentWriter writer)
    {
        if (tree instanceof List) {
            writer.print "("
            def indent = writer + 1
            if (!tree.isEmpty()) {
                def head = tree.head()
                if (head instanceof List) {
                    printTree(head, indent)
                } else {
                    writer.print sexpString(head.toString())
                }
                def tail = tree.tail()
                tail.each { if (tail.size() > 1) indent.println() ; printTree(it, indent) }
            }
            indent.print ")"
        } else {
            writer.print " " + sexpString(tree.toString())
//        writer.print " '$tree'"
        }
    }

    static def sexpString(String str)
    {
        str = str.replace("(", "\\(").replace(")", "\\)").replace("\"", "\\\"")
        (!str || str.contains(" ") || str.contains("\\")) ? "\"" + str + "\"" : str
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv)
    {
        if (!roundEnv.processingOver()) {
            builder.hr()
            def elements = roundEnv.getRootElements().sort { it.simpleName };
            for (Element each : elements) {
                printElement(0, each);
            }
            builder.p("$includedElements matched.")
        }

        return false;
    }
}
