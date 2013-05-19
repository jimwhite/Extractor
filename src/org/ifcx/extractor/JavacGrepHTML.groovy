package org.ifcx.extractor

import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.CompoundAssignmentTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import com.sun.tools.javac.comp.AttrContext
import com.sun.tools.javac.comp.Enter
import com.sun.tools.javac.comp.Env
import com.sun.tools.javac.comp.MemberEnter
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeInfo
import com.sun.tools.javac.tree.TreeMaker

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
import javax.lang.model.type.TypeVariable
import javax.lang.model.util.Elements
import groovy.xml.MarkupBuilder
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import org.ifcx.extractor.util.IndentWriter

import java.util.regex.Pattern

import com.sun.source.tree.LineMap
import com.sun.tools.javac.util.Context
import com.sun.source.tree.LiteralTree
import com.sun.tools.javac.comp.Attr
import com.sun.tools.apt.main.JavaCompiler

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavacGrepHTML extends AbstractProcessor
{
    MarkupBuilder builder
    Integer includedElements = 0

//    DocEnv docEnv

    JavaCompiler compiler

    Attr    attr

    Trees trees;
    TreeMaker make;
    Elements elems;
    Enter enter;
    MemberEnter memberEnter

    LineMap lineMap
    def topLevelEnv
    Env<AttrContext> topLevel
//    def compilationUnits = new  ListBuffer<JCCompilationUnit>()

    BodyChecker bodyChecker = new BodyChecker()


//    WritableWorkbook workbook = Workbook.createWorkbook(new File("tmp/output.xls"));

    public JavacGrepHTML(Context context, MarkupBuilder b)
    {
//        docEnv = DocEnv.instance(context)
        compiler = JavaCompiler.instance(context)
        attr = Attr.instance(context)
        enter = Enter.instance(context)
        memberEnter = MemberEnter.instance(context)

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

//    Map<String, String> mappings = new TreeMap<String, String>() {{
//        put("urn:org.ifcx.model#", "model");
//        put(XMLSchema.NAMESPACE, "xsd");
//    }};

    final String padding = "                                                                         ";
    final Pattern paramPattern = ~/@param\s+(\w+)\s*(.*)$/

    void printElement(Element element)
    {
        try {
            topLevel = null
            topLevelEnv = null

//            enter.complete(com.sun.tools.javac.util.List.of(topLevel), null)

            if (element.kind == ElementKind.CLASS) {
//                def compilationUnit = trees.getTree((TypeElement) element)
                topLevel = enter.getEnv(element)
//                def topLevelJCU = topLevel.toplevel
//                topLevelEnv = enter.getTopLevelEnv(topLevelJCU)

//                compiler.attribute(topLevel)

//                attr.attribClass(env.tree.pos(), env.enclClass.sym)
//                compiler.attribute(env)
            }


//            compilationUnits.add(topLevel)
//            enter.complete(compilationUnits.toList(), null)

//            lineMap = topLevel.lineMap

            _printElement(0, element)
        } catch (Exception e) {
            e.printStackTrace()
            System.err.println "Exception in ${element}"
        }
    }

    void _printElement(int indent, Element element)
    {
        String docComment = elems.getDocComment(element);
        if (docComment != null && (element.kind == ElementKind.METHOD) && (trees.getTree(element) != null)) {
            Symbol.MethodSymbol method = element
            JCTree.JCMethodDecl methodTree = (MethodTree) trees.getTree(method)
            def simpleBody = bodyChecker.scan((Tree) methodTree.body, null)

//            if (simpleBody) {
//                println method.simpleName
//            }

/*
            if (method.simpleName.contentEquals("typeMismatch")) {
                println method.simpleName
            }
            try {
                bodyChecker.scan(trees.getTree(method).body)
            } catch (NullPointerException npe) {
                println method.simpleName
                npe.printStackTrace()
            }
*/


            if (simpleBody && isSingleStatement(methodTree)) {
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

//                if (docEnv == null) return ;
//                def docImpl = new MethodDocImpl(docEnv, method, docComment, trees.getTree(element), lineMap)
//                def methodComment = docImpl.commentText().trim()
//                if (!methodComment) return ;
//
//                def returnTags = docImpl.tags('return')
//                if (returnTags.size()) returnComment = returnTags[0].text()
//
//                docImpl.paramTags().each { ParamTag tag ->
//                    paramComments[tag.parameterName()] = tag.parameterComment()
//                }

//                if (element.simpleName.contentEquals("typeMismatch")) {
//                    println "hey!"
//                }

                ++includedElements
                builder.div('class':'method') {
                    code('class':'method-id', methodId(methodTree))

                    if (docComment) {
                        p {
                            pre('class':'method-docComment', docComment)
                        }
                    }

//                    div(style:'font-size:110%;') {
//                        yield(methodComment, false)
//                    }

                    div {
                        code('class':'method-enclosing-element', fullName(element.enclosingElement))
                    }

                    div {
                        code('class':'method-name', fullName(element))
                    }

                    methodTree.parameters.each { paramTree ->
                        div('class':'method-parameter') {
                            try {
//                            if (paramTree.vartype.type.properties.containsKey('tsym')) {
                                code('class':'methodTree-parameter-name', paramTree.name)
                                code('class':'methodTree-parameter-vartype', paramTree.vartype.@'type'.tsym.flatName())
//                            } else {
//                                code(paramTree.vartype.type)
//                            }
                            } catch (Throwable t) {
                                println(paramTree)
                                println(paramTree.vartype)
                                println(paramTree.vartype.@'type')
                                if (paramTree.vartype.type instanceof JCTree.JCTypeApply) {
                                    JCTree.JCTypeApply tca = paramTree.vartype.@'type'
                                    Type.ClassType ct = tca.@'type'
                                    println ct
                                }
                                t.printStackTrace()
                            }
                        }
                    }

                    div {
                        code('class':'method-returnType', method.returnType)
                    }

                    if (method.typeParameters) {
                        method.typeParameters.each { typeParam ->
                            div {
                                code('class':'method-typeParameter', typeParam)
                            }
                        }
                    }

                    if (method.thrownTypes) {
                        method.thrownTypes.each { thrownType ->
                            div {
                                code('class':'method-thrownType', thrownType)
                            }
                        }
                    }

                    div {
                        code(method.returnType)
                        code(element.simpleName + "(" + method.parameters.simpleName.join(', ') + ")")
                        span([style:'font-size:110%;'], returnComment)
                    }

                    for (VariableElement var : method.getParameters()) {
                        div {
//                            code(var.getSimpleName() + " : " + var.asType() + " ")
                            code('class':'method-parameter-type', var.asType())
                            code('class':'method-parameter-name', var.simpleName)
                            span([style:'font-size:110%;'], paramComments[var.simpleName.toString()] ?: "")
                            br()
                        }
                    }

                    printMethod(indent, method);

                    hr()
                }
            }
        }

        for (Element each : element.getEnclosedElements()) {
            _printElement(indent + 2, each);
        }

    }

    String methodId(JCTree.JCMethodDecl methodTree)
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

        def returnTypeName = methodTree.returnType.@'type'.tsym.flatName()
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

    protected boolean isReturnArrayAccess(MethodTree tree)
    {
        ((tree.body?.statements?.size() == 1)
            && (tree.body.statements[0].kind == Tree.Kind.RETURN )
            && (tree.body.statements[0].expression?.kind == Tree.Kind.ARRAY_ACCESS ))

    }

    protected boolean isReturnVariable(MethodTree tree)
    {
        ((tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.RETURN )
                && (tree.body.statements[0].expression?.kind == Tree.Kind.MEMBER_SELECT ))

    }

    protected boolean isReturnLiteral(MethodTree tree)
    {
        ((tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.RETURN )
                && (tree.body.statements[0].expression instanceof LiteralTree))

    }

    protected boolean isSingleAssignment(MethodTree tree)
    {
        ((tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.EXPRESSION_STATEMENT )
                && (tree.body.statements[0].expression instanceof AssignmentTree ))
//                && (tree.body.statements[0].expression.kind == Tree.Kind.ASSIGNMENT ))

    }

    protected boolean isSingleCompoundAssignment(MethodTree tree)
    {
/*
        def compoundAssignmentKinds = [Tree.Kind.AND_ASSIGNMENT, Tree.Kind.OR_ASSIGNMENT
                , Tree.Kind.PLUS_ASSIGNMENT, Tree.Kind.MINUS_ASSIGNMENT
                , Tree.Kind.DIVIDE_ASSIGNMENT, Tree.Kind.MULTIPLY_ASSIGNMENT
                , Tree.Kind.LEFT_SHIFT_ASSIGNMENT, Tree.Kind.RIGHT_SHIFT_ASSIGNMENT, Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT
        ] as Set
*/

        ((tree.body?.statements?.size() == 1)
                && (tree.body.statements[0].kind == Tree.Kind.EXPRESSION_STATEMENT )
                && (tree.body.statements[0].expression instanceof CompoundAssignmentTree))
//        && (tree.body.statements[0].expression.kind in compoundAssignmentKinds))

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

        builder.pre('class':'method-body', tree.body)

//        def methodEnv = memberEnter.getMethodEnv(tree, topLevelEnv)
//
//        attr.attribStat(tree.body, methodEnv)

//        attr.attribStat(tree.body, enter.getEnv(method))

//        def topLevel = method.enclosingElement
//        while (!(topLevel instanceof CompilationUnitTree)) { topLevel = topLevel.enclosingElement }
//        topLevel = trees.getTree(topLevel)

//        def methodEnv = enter.getClassEnv(trees.getTree((TypeElement) method.enclosingElement))
//
//        tree.body.statements.each { attr.attribStat(it, methodEnv) }

        Object sexp = externalize(tree)

        StringWriter sw = new StringWriter()
        sw.withPrintWriter { printTree(sexp, new IndentWriter(it) ) }
        builder.pre('class':'method-body-externalized', sw)
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
                obj << symbol_info(tree)
            }

        } else if (obj instanceof List) {
            obj = obj.isEmpty() ? [] : ['LIST'] + obj.collect { externalize(it) }
        } else if (obj instanceof Set) {
            obj = obj.isEmpty() ? [] : ['SET'] + obj.collect { externalize(it) }
        }

        obj
    }

    def symbol_info(JCTree ident)
    {
        Symbol sym = TreeInfo.symbolFor(ident)

        if (sym == null) sym = TreeInfo.symbol(ident)

        if (sym == null) {
            if ((ident instanceof JCTree.JCIdent) || (ident instanceof JCTree.JCFieldAccess)) {
                try {
                    sym = attr.attribIdent(ident, topLevel.toplevel)
                } catch (NullPointerException npe) {
                    // This fails when the selected of the MemberSelect (FieldAccess) doesn't resolve
                    // to a symbol, such as when a method call or other expression is evaluated to
                    // get the container for the members.
                    // Probably the correct thing for MemberSelect is to get the type of selected.
//                    println ident.name
                }
            }

//            } else if (ident instanceof JCTree.JCFieldAccess) {
//                def type = attr.attribExpr(ident, methodEnv)
//                attr.visitSelect(ident)
//                sym = ident.sym
        }

        def r = ['SYM']

        if (sym != null) {
//            r << sym.name
            r << sym.flatName()
            def flags = Flags.asModifierSet(sym.flags())
            if (!flags.empty) r << ['FLAGS'] + flags
            if (sym.attributes_field.nonEmpty()) r << ['ATTR'] + sym.attributes_field.toString()
//            if (sym.owner != null && sym.owner.name.toString()) { r << ['OWNER', sym.owner.name, sym.owner.flatName()] }
            if (sym.owner != null && sym.owner.name.toString()) {
                def owner_tree = trees.getTree(sym.owner)
                if (owner_tree != null) r << ['OWNER', symbol_info(owner_tree)]
            }
        } else {
            r << '*none*'
        }

        r
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
//                    Symbol sym = compiler.resolveIdent(((TypeElement)each).qualifiedName.toString())
//                }
                printElement(each);
            }
            builder.p("$includedElements matched.")
        }

        return false;
    }
}
