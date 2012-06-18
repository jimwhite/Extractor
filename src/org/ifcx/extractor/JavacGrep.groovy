package org.ifcx.extractor

import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.annotation.processing.AbstractProcessor
import javax.tools.JavaFileManager
import javax.tools.StandardLocation
import com.tinkerpop.blueprints.pgm.Graph
import com.tinkerpop.frames.FramesManager
import com.sun.source.util.Trees
import com.sun.tools.javac.tree.TreeMaker
import javax.lang.model.util.Elements
import javax.annotation.processing.ProcessingEnvironment
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import org.openrdf.model.vocabulary.XMLSchema
import javax.lang.model.element.Element
import com.sun.tools.javac.code.Symbol
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.element.TypeElement
import javax.annotation.processing.RoundEnvironment
import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree
import com.sun.source.util.TreePath
import com.sun.tools.javac.tree.JCTree

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavacGrep extends AbstractProcessor
{
    protected PrintWriter printer;

    private Trees trees;
    private TreeMaker make;
    private Elements elems;

    private File rdfDir = new File("rdf");

    public JavacGrep(PrintWriter pw)
    {
        printer = pw;
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

            if (includeElement(element)) {
                printer.print(padding.substring(0, indent) + element.getKind() + " " + method.returnType.toString() + " ")
                printer.println(element.toString() + " " + element.getModifiers());

                printer.print(padding.substring(0, indent + 4));
                for (VariableElement var : method.getParameters()) {
                    printer.print(var.getSimpleName() + ":" + var.asType() + " ");
                }
                printer.println();

                printer.println(docComment);

                printMethod(indent, method);
            }
        }

        for (Element each : element.getEnclosedElements()) {
            printElement(indent + 2, each);
        }
    }

    protected boolean includeElement(ExecutableElement method)
    {
        MethodTree tree = trees.getTree(method)

        ((tree.body?.statements?.size() == 1)
            && (tree.body.statements[0].kind == Tree.Kind.EXPRESSION_STATEMENT )
            && (tree.body.statements[0].expression.kind == Tree.Kind.ASSIGNMENT ))

    }

    protected void printMethod(indent, ExecutableElement method)
    {
        def sw = new StringWriter()
        Pretty visitor = new Pretty(sw, false);
        MethodTree tree = trees.getTree(method)
        visitor.printStat(tree.body)
        sw.toString().eachLine { printer.println(padding.substring(0, indent) + it) }
        printer.println "\n"
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv)
    {
        if (!roundEnv.processingOver()) {
            def elements = roundEnv.getRootElements().sort { it.simpleName };
            printer.println("---===---");
            printer.println("There are " + elements.size() + " root elements.");
            printer.println();

            for (Element each : elements) {
                printElement(0, each);
            }

            printer.println("---===---");
        }

        return false;
    }
}
