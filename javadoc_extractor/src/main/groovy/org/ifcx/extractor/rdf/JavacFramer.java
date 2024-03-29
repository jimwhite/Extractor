package org.ifcx.extractor.rdf;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.FramesManager;

import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.rdfa.RDFaParser;
import org.openrdf.rio.rdfa.RDFaParserFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavacFramer extends AbstractProcessor
{
    protected PrintWriter printer;

    public static JavaFileManager.Location rdfLocation = StandardLocation.locationFor("RDF_OUTPUT");
    public static java.util.List<File> rdfPath = Arrays.asList(new File("rdf"));

    private Graph graph;
    private FramesManager manager;

    private Trees trees;
    private TreeMaker make;
    private Elements elems;

    private File rdfDir = new File("rdf");

    public JavacFramer(PrintWriter pw)
    {
        printer = pw;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        graph = TinkerGraphFactory.createTinkerGraph();
        manager = new FramesManager(graph);

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
        if (docComment != null) {
            printer.print(padding.substring(0, indent) + element.getKind() + (element instanceof Symbol ? " * " : " "));
            printer.println(element + " " + element.getModifiers());
//            if (element instanceof Symbol.MethodSymbol) {
//                Symbol.MethodSymbol method = (Symbol.MethodSymbol) element;
//                printer.print(padding.substring(0, indent + 4));
//                for (Symbol.VarSymbol var : method.getParameters()) {
//                    printer.print(var.getQualifiedName() /*+ ":" + var.asType()*/ + " ");
//                };
//                printer.println();
//    //            printer.println(((Symbol.MethodSymbol) element).savedParameterNames);
//            }
            if (element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                printer.print(padding.substring(0, indent + 4));
                for (VariableElement var : executableElement.getParameters()) {
                    printer.print(var.getSimpleName() + ":" + var.asType() + " ");
                }
                printer.println();
        //            printer.println(((Symbol.MethodSymbol) element).savedParameterNames);
            }

            printer.println(docComment);
        }

        for (Element each : element.getEnclosedElements()) {
            printElement(indent + 2, each);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv)
    {
        if (!roundEnv.processingOver()) {
            Set<? extends Element> elements = roundEnv.getRootElements();
            printer.println("---===---");
            printer.println("There are " + elements.size() + " root elements.");
            printer.println();

            for (Element each : elements) {
                printElement(0, each);
                printer.println("---");
            }

            printer.println("---===---");
        }

        return false;
    }
}
