package org.ifcx.extractor

import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.comp.AttrContext
import com.sun.tools.javac.comp.Enter
import com.sun.tools.javac.comp.Env
import com.sun.tools.javac.comp.MemberEnter
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit
import com.sun.tools.javac.tree.TreeInfo
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.ListBuffer

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
        if (docComment != null && (element instanceof ExecutableElement)) {
            ExecutableElement method = (ExecutableElement) element;

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

            def simpleBody = bodyChecker.scan((Tree) trees.getTree(method).body, null)
//            if (simpleBody) {
//                println method.simpleName
//            }

            if (simpleBody && includeSingleAssignment(element)) {
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

                if (element.simpleName.contentEquals("typeMismatch")) {
                    println "hey!"
                }

                ++includedElements
                builder.div() {
                    p {
                        pre(docComment)
                    }

//                    div(style:'font-size:110%;') {
//                        yield(methodComment, false)
//                    }

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

        for (Element each : element.getEnclosedElements()) {
            _printElement(indent + 2, each);
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

        ((method.kind == ElementKind.METHOD) && (tree?.body?.statements?.size() == 1)
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
                    println ident.name
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
