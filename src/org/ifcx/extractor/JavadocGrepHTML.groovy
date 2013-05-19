package org.ifcx.extractor

import com.sun.javadoc.ParamTag
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.LineMap
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.VariableTree
import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import com.sun.tools.javac.comp.Attr
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Context
import com.sun.tools.javadoc.DocEnv
import com.sun.tools.javadoc.MemberDocImpl
import com.sun.tools.javadoc.MethodDocImpl
import groovy.xml.MarkupBuilder
import org.ifcx.extractor.util.IndentWriter

import java.util.regex.Pattern
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements

import com.sun.tools.javadoc.ModifierFilter

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavadocGrepHTML extends AbstractProcessor
{
    MarkupBuilder builder
    Integer includedElements = 0

    DocEnv docEnv

//    JavaCompiler compiler

    Attr    attr

    Trees trees;
    TreeMaker make;
    Elements elems;

    LineMap lineMap

    public JavadocGrepHTML(Context context, MarkupBuilder b)
    {
        docEnv = DocEnv.instance(context)
        if (docEnv.showAccess == null) docEnv.showAccess = new ModifierFilter(0);

//        compiler = JavaCompiler.instance(context)
        attr = Attr.instance(context)

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

    final String padding = "                                                                         ";
    final Pattern paramPattern = ~/@param\s+(\w+)\s*(.*)$/

    void printElement(int indent, Element element)
    {
        try {
            _printElement(indent, element)
        } catch (Exception e) {
            e.printStackTrace()
            System.err.println "Exception in ${element}"
        }
    }

    void _printElement(int indent, Element element)
    {
        String docComment = elems.getDocComment(element);
        if (docComment != null && (element instanceof ExecutableElement)) {
            ExecutableElement method = (ExecutableElement) element;

            if (includeElement(element)) {
                def docLines = []
                def paramComments = [:]
                def returnComment = ''
                docComment.eachLine { line ->
                    line = line.trim()
                    if (line.startsWith('@param')) {
                        def m = paramPattern.matcher(line)
                        if (m.find()) {
                            paramComments[m.group(1)] = m.group(2)
                        }
                    } else if (line.startsWith('@return')) {
                        returnComment = line.substring('@return'.length()).trim()
                    } else {
                        if (line) docLines << line
                    }
                }

                docComment = docLines.join('\n')

                if (!docComment) return ;

                if (docEnv == null) return ;
                def docImpl = new MethodDocImpl(docEnv, method, docComment, trees.getTree(element), lineMap)
                def methodComment = docImpl.commentText().trim()
                if (!methodComment) return ;

                def returnTags = docImpl.tags('return')
                if (returnTags.size()) returnComment = returnTags[0].text()

                docImpl.paramTags().each { ParamTag tag ->
                    paramComments[tag.parameterName()] = tag.parameterComment()
                }

                def docCommentCSS = 'font-size:110%;font-weight:bold;font-style:italic'

                ++includedElements
                builder.div() {
//                    div('class':'docComment') {
//                        pre(docComment)
//                    }

                    div('class':'methodComment', style:docCommentCSS) {
                        mkp.yield(methodComment)
                    }

                    div('class':'enclosingElementName') {
                        code(fullName(element.enclosingElement))
                    }

                    div('class':'methodDeclaration') {
                        code(method.returnType)
                        code(element.simpleName + "(" + method.parameters.simpleName.join(', ') + ")")
                        span([style:docCommentCSS], returnComment)
                    }

                    div('class':'parameterDeclarations') {
                        for (VariableElement var : method.getParameters()) {
//                            code(var.getSimpleName() + " : " + var.asType() + " ")
                            code(var.asType())
                            code(var.simpleName)
                            span([style: docCommentCSS], paramComments[var.simpleName.toString()] ?: "")
                            br()
                        }
                    }

                    div('class':'body') {
                        printMethod(indent, method)
                    }

                    div('class':'fields') {
                        Element scope = method

                        while (scope != null && scope.kind != ElementKind.CLASS) {
                            scope = scope.enclosingElement
                        }

                        scope.enclosedElements.each { Element member ->
                            if (member.kind in [ElementKind.FIELD]) {
                                def varComment = elems.getDocComment(member)?.trim()
                                div('class':'field') {
                                    code(member)
                                    code(member.modifiers)
                                    if (varComment) span([style:docCommentCSS], varComment)

                                    Object sexp = externalize(trees.getTree(member))

                                    def msw = new StringWriter()
                                    msw.withPrintWriter { printTree(sexp, new IndentWriter(it) ) }
                                    pre(msw)
                                }
                            }
                        }
                    }

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

    protected boolean includeElement(ExecutableElement method)
    {
//        includeReturnArrayAccess(method) || includeMethodInvocation(method) || includeReturnVariable(method) || includeReturnLiteral(method) || includeSingleAssignment(method)

//        includeReturnArrayAccess(method) || includeReturnVariable(method) || includeReturnLiteral(method) || includeSingleAssignment(method)

//        includeMethodInvocation(method)
//        includeReturnArrayAccess(method)
//        includeReturnVariable(method)
//        includeReturnLiteral(method)
        includeSingleAssignment(method)
//        includeSingleIf(method)
//        includeSingleWhile(method)
//        includeSingleFor(method)
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

    protected boolean includeMethodInvocation(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.EXPRESSION_STATEMENT )
                && (tree.body.statements[0].expression.kind == Tree.Kind.METHOD_INVOCATION ))

    }

    protected boolean includeSingleIf(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.IF ))

    }

    protected boolean includeSingleFor(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind in [ Tree.Kind.FOR_LOOP, Tree.Kind.ENHANCED_FOR_LOOP ] ))

    }

    protected boolean includeSingleWhile(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((method.kind == ElementKind.METHOD) && (tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind in [ Tree.Kind.DO_WHILE_LOOP, Tree.Kind.WHILE_LOOP ] ))

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

//    protected void printMethod(indent, ExecutableElement method)
//    {
//        MethodTree tree = trees.getTree(method)
//
//        builder.pre(tree.body)
//
//        Object sexp = externalize(tree)
//
//        StringWriter sw = new StringWriter()
//        sw.withPrintWriter { printTree(sexp, new IndentWriter(it) ) }
//        builder.pre(sw)
//    }

    protected void printMethod(indent, ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        builder.pre(tree.body)

        def sw = new StringWriter()
        new LambdaFormatter(sw, false).printStat(tree.body)
        builder.pre(sw)

        sw = new StringWriter()
        sw.withPrintWriter { printTree(externalize(tree), new IndentWriter(it) ) }
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
            if (tree.class.declaredFields.find { it.name == 'sym' }) {
                obj << ['SYM', externalize(tree.sym)]
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
//                if (each.kind == ElementKind.CLASS) {
//                    Symbol sym = compiler.resolveIdent(each.qualifiedName)
//                }
                printElement(0, each);
            }
            builder.p("$includedElements matched.")
        }

        return false;
    }
}
