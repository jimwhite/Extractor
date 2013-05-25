package org.ifcx.extractor

import com.sun.javadoc.ParamTag
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.LineMap
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.VariableTree
import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import com.sun.tools.javac.comp.Attr
import com.sun.tools.javac.comp.AttrContext
import com.sun.tools.javac.comp.Enter
import com.sun.tools.javac.comp.Env
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeInfo
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.util.Log
import com.sun.tools.javadoc.DocEnv
import com.sun.tools.javadoc.MemberDocImpl
import com.sun.tools.javadoc.MethodDocImpl
import groovy.xml.MarkupBuilder
import org.ifcx.extractor.util.IndentWriter

import javax.lang.model.element.Name
import javax.lang.model.type.TypeVariable
import javax.tools.JavaFileObject
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
    Integer includedElements = 0

    MarkupBuilder builder

    File abstracts_file

    DocEnv docEnv

//    JavaCompiler compiler

    Attr    attr
    Enter   enter
    Log compilerLog

    Trees trees;
    TreeMaker make;
    Elements elems;

    JavaFileObject  sourceFile
    LineMap lineMap

    BodyChecker bodyChecker = new BodyChecker()

    public JavadocGrepHTML(Context context, MarkupBuilder b)
    {
        docEnv = DocEnv.instance(context)
        if (docEnv.showAccess == null) docEnv.showAccess = new ModifierFilter(0);

//        compiler = JavaCompiler.instance(context)
        attr = Attr.instance(context)
        compilerLog = Log.instance(context)
        enter = Enter.instance(context)

        builder = b

        abstracts_file = new File("tmp/method_abstracts.txt")
        abstracts_file.write("")
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
            if (element instanceof TypeElement && element instanceof Symbol.ClassSymbol) {
/*
                def tree = trees.getTree((TypeElement) element)
                if (tree instanceof Symbol.ClassSymbol) {
                    sourceFile = tree.sourcefile
                    def topLevel = enter.getClassEnv(element)
                    lineMap = topLevel.toplevel.lineMap
                }
*/

                    sourceFile = element.sourcefile
                    def topLevel = enter.getClassEnv(element)
                    lineMap = topLevel.toplevel.lineMap

                _printElement(indent, element)
            }
        } catch (Exception e) {
            e.printStackTrace()
            System.err.println "Exception in ${element}"
        }
    }

    void _printElement(int indent, Element element)
    {
        def tree = trees.getTree(element)

        String docComment = elems.getDocComment(element);
        if (docComment != null && (element instanceof Symbol.MethodSymbol) && (tree instanceof JCTree.JCMethodDecl)) {
            Symbol.MethodSymbol method = element
            JCTree.JCMethodDecl methodTree = tree

            def simpleBody = bodyChecker.scan((Tree) methodTree.body, null)

            if (simpleBody && isSingleStatement(methodTree)) {
                def docLines = []
                def paramComments = [:]
                def returnComment = ''
                def paramContinuation = null
                def returnContinuation = false
                docComment.eachLine { line ->
                    line = line.trim()
                    if (line) {
                        if (line.startsWith('@param')) {
                            paramContinuation = null
                            returnContinuation = false
                            def m = paramPattern.matcher(line)
                            if (m.find()) {
                                paramContinuation = m.group(1)
                                paramComments[paramContinuation] = m.group(2)
                            }
                        } else if (line.startsWith('@return')) {
                            paramContinuation = null
                            returnContinuation = true
                            returnComment = line.substring('@return'.length()).trim()
                        } else {
                            if (returnContinuation) {
                                returnComment += "\n" + line
                            } else if (paramContinuation) {
                                paramComments[paramContinuation] = paramComments[paramContinuation] + "\n" + line
                            } else {
                                docLines << line
                            }
                        }
                    }
                }

                docComment = docLines.join('\n')

                if (!docComment) return ;

                if (docEnv == null) return ;
                def docImpl = new MethodDocImpl(docEnv, method, docComment, methodTree, lineMap)
                def methodComment = docImpl.commentText().trim()
                if (!methodComment) return ;

                def returnTags = docImpl.tags('return')
                if (returnTags.size()) returnComment = returnTags[0].text()

                docImpl.paramTags().each { ParamTag tag ->
                    paramComments[tag.parameterName()] = tag.parameterComment()
                }

                def docCommentCSS = 'font-size:110%;font-weight:bold;font-style:italic'

                ++includedElements
                builder.div('class':'method') {
                    code('class':'method-id', methodId(methodTree))

/*
                    if (docComment) {
                        p {
                            pre('class':'method-docComment', docComment)
                        }
                    }
*/

                    div('class':'method-docComment', style:docCommentCSS) {
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
                        printMethod(indent, element, docComment, returnComment, paramComments)
                    }

/*
                    div('class':'fields') {
                        Element scope = element

                        while (scope != null && scope.kind != ElementKind.CLASS && scope.enclosingElement != null) {
                            scope = scope.enclosingElement
                        }

                        scope?.enclosedElements?.each { Element member ->
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
*/

                    hr()
                }
            }
        }

        for (Element each : element.getEnclosedElements()) {
            _printElement(indent + 2, each);
        }
    }

    String methodId(MethodTree methodTree)
    {
        def typeVariables = methodTree.parameters.grep { it.vartype.@'type' instanceof TypeVariable }
        def typeVariableNames = typeVariables.collectEntries {
            Type.TypeVar vtt = it.vartype.@'type'
            [vtt.tsym.flatName(), '~' + vtt.upperBound.tsym.flatName() + '~' + vtt.lowerBound.tsym.flatName()] }

        def nameToType = { typeVariableNames.containsKey(it) ? typeVariableNames[it] : it }

        def parameterTypes = methodTree.parameters.collect {
            def n = it.vartype.@'type'.tsym.flatName()
            nameToType(n)
        }

        // Constructors have a null return type
        def returnTypeName = methodTree.returnType != null ? methodTree.returnType.@type.tsym.flatName() : "<init>"

        ['method', fullName(methodTree.sym.enclosingElement), methodTree.sym.qualifiedName, nameToType(returnTypeName), parameterTypes.size() as String, *parameterTypes].join('|')
    }


    static String fullName(Element element)
    {
        ((element.enclosingElement == null) ? "" : fullName(element.enclosingElement) + ".") + (element instanceof PackageElement ? element.qualifiedName : element.simpleName)
    }

    protected boolean isSingleStatement(MethodTree tree)
    {
        (tree.body?.statements?.size() == 1)
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

    protected void printMethod(indent, ExecutableElement method, String methodComment, String returnComment, Map paramComments)
    {
        MethodTree tree = trees.getTree(method)

        builder.pre('class':'method-body-source', tree.body)

//        def sw = new StringWriter()
//        new LambdaFormatter(sw, false).printStat(tree.body)
//        builder.pre(sw)

        def sw = new StringWriter()
        sw.withPrintWriter { printTree(abstracted_tree(method, tree, methodComment, returnComment, paramComments), new IndentWriter(it) ) }
        builder.pre('class':'method-body-tree', sw)

        if (abstracts_file != null) abstracts_file << sw.toString() + "\n\n"
    }

    def abstract_tree_variables = []

    def new_abstract_tree_variable(Name name)
    {
        def new_var = ["IDENTIFIER", abstract_tree_variables.size()]

        abstract_tree_variables << [new_var, ['Name', name.toString()]]

        new_var
    }

    def abstracted_tree(ExecutableElement method, MethodTree tree, String methodComment, String returnComment, Map paramComments)
    {
        abstract_tree_variables = []

        def res = ["METHOD", ["Id", methodId(tree)]]

        if (sourceFile != null) {
            res << ["SourceURI", sourceFile.toUri().toString()]
            res << ["StartPosition", TreeInfo.getStartPos(tree)]
            if (lineMap != null) {
                res << ["StartLine", lineMap.getLineNumber(TreeInfo.getStartPos(tree))]
            }
//            res << ["EndPosition", tree.endPosition]
        }

        if (methodComment) res << ["Comment", methodComment]

        res << ["Name", method.simpleName.toString()]
        res << ["Enclosure", fullName(method.enclosingElement)]

        res << ["Parameters", externalize(tree.parameters)]
        if (paramComments) res << ["ParameterComments", paramComments.collect { k, v -> [["Name", k], ["Comment", v]] }]

        res << ["ReturnType", externalize(tree.returnType)]
        if (returnComment) res << ["ReturnComment", returnComment]

        res << ["JavaSource", tree.toString()]

        res << ["Vars", abstract_tree_variables]
        res << ["Tree", _abstract_tree(tree.body.statements[0])]

        res
    }

    def _abstract_tree(Object obj) {
        if (obj instanceof Tree) {
            Tree tree = obj
            if (tree.kind == Tree.Kind.IDENTIFIER) {
//                def ident = ['Name', tree.name]
//                if (tree.sym != null) ident << ['SYM', tree.sym.qualifiedName]
                obj = new_abstract_tree_variable(tree.name)
            } else {
                obj = [tree.kind, /*["Source", tree.toString() *//*((JCTree) tree).startPosition as String*//*]*/]
                def treeKlazz = tree.class.interfaces.find { Tree.class.isAssignableFrom(it) }
                if (!treeKlazz) {
                    return tree
                }
                treeKlazz.declaredMethods.sort { it.name }.each { propertyMethod ->
                    def name = propertyMethod.name
                    // ${Tree.isAssignableFrom(propertyMethod.returnType)} ${treeListType.isAssignableFrom(propertyMethod.returnType)} ${propertyMethod} ${propertyMethod.returnType}"
                    if (name.equals("getIdentifier")) {
                        obj << ["Identifier", new_abstract_tree_variable(propertyMethod.invoke(tree))]
                    } else {
                        if (name.startsWith('get')) {
                            obj << [name.substring(3), _abstract_tree(propertyMethod.invoke(tree))]
                        } else if (name.startsWith('is')) {
                            obj << [name.substring(2), _abstract_tree(propertyMethod.invoke(tree))]
                        }
                    }
                }
            }
        } else if (obj instanceof List) {
            obj = obj.isEmpty() ? [] : ['LIST'] + obj.collect { _abstract_tree(it) }
        } else if (obj instanceof Set) {
            obj = obj.isEmpty() ? [] : ['SET'] + obj.collect { _abstract_tree(it) }
        }

        obj
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
//                        if (name.equals("getModifiers")) {
//                            println tree
//                        }
                        def v = externalize(propertyMethod.invoke(tree))
                        if (v) obj << [name.substring(3), v]
                    } else if (name.startsWith('is')) {
                        def v = externalize(propertyMethod.invoke(tree))
                        if (v) obj << [name.substring(2), v]
                    }
                }
            if (tree.class.declaredFields.find { it.name == 'sym' }) {
                obj << ['SYM', externalize(tree.sym)]
            }
            if (obj.size() == 1) obj = []
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
