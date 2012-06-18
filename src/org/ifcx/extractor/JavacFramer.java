package org.ifcx.extractor;

import com.sun.org.apache.xerces.internal.impl.Constants;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.FramesManager;
import org.ifcx.extractor.util.RDFaWriter;

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
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JavacFramer extends AbstractProcessor
{
    public static JavaFileManager.Location rdfLocation = StandardLocation.locationFor("RDF_OUTPUT");
    public static java.util.List<File> rdfPath = Arrays.asList(new File("rdf"));

    private Graph graph;
    private FramesManager manager;

    private Trees trees;
    private TreeMaker make;
    private Elements elems;

    private File rdfDir = new File("rdf");

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
            System.out.print(padding.substring(0, indent) + element.getKind() + (element instanceof Symbol ? " * " : " "));
            System.out.println(element + " " + element.getModifiers());
//            if (element instanceof Symbol.MethodSymbol) {
//                Symbol.MethodSymbol method = (Symbol.MethodSymbol) element;
//                System.out.print(padding.substring(0, indent + 4));
//                for (Symbol.VarSymbol var : method.getParameters()) {
//                    System.out.print(var.getQualifiedName() /*+ ":" + var.asType()*/ + " ");
//                };
//                System.out.println();
//    //            System.out.println(((Symbol.MethodSymbol) element).savedParameterNames);
//            }
            if (element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                System.out.print(padding.substring(0, indent + 4));
                for (VariableElement var : executableElement.getParameters()) {
                    System.out.print(var.getSimpleName() + ":" + var.asType() + " ");
                }
                System.out.println();
        //            System.out.println(((Symbol.MethodSymbol) element).savedParameterNames);
            }

            System.out.println(docComment);
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
            System.out.println("---===---");
            System.out.println("There are " + elements.size() + " root elements.");
            System.out.println();

            for (Element each : elements) {
                printElement(0, each);
                System.out.println("---");
            }

            System.out.println("---===---");
        }

        return false;
    }
}
