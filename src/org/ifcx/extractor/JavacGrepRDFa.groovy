package org.ifcx.extractor

import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.TreeMaker
import groovy.xml.MarkupBuilder
import org.openrdf.model.vocabulary.XMLSchema

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

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavacGrepRDFa extends AbstractProcessor
{
    MarkupBuilder builder
    Integer includedElements = 0

    private Trees trees;
    private TreeMaker make;
    private Elements elems;

    public JavacGrepRDFa(MarkupBuilder b)
    {
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

    private void printElement(int indent, Element element)
    {
        String docComment = elems.getDocComment(element);
        if (docComment != null && (element instanceof ExecutableElement)) {
            ExecutableElement method = (ExecutableElement) element;

            if (includeSingleAssignment(element)) {
                ++includedElements
                builder.div() {
                    p {
                        docComment = docComment.replace("@", "<br/>@")
                        yield(docComment, false)
                    }

                    div {
                        code(fullName(element.enclosingElement))
                    }

                    div(/*element.getKind()*/) {
                        code(method.returnType)
                        code(element.simpleName + "(" + method.parameters.simpleName.join(', ') + ")")
//                        span(element.modifiers)
                    }

                    div {
                        for (VariableElement var : method.getParameters()) {
//                            code(var.getSimpleName() + " : " + var.asType() + " ")
                            code(var.asType())
                            code(var.simpleName)
                            br()
                        }
                    }

                    printMethod(indent, method);

                    hr()
                }
            }
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
