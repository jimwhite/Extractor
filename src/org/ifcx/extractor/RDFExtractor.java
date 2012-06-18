package org.ifcx.extractor;


import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
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
public class RDFExtractor extends AbstractProcessor
{
    public static JavaFileManager.Location rdfLocation = StandardLocation.locationFor("RDF_OUTPUT");
    public static java.util.List<File> rdfPath = Arrays.asList(new File("rdf"));

    private int tally = 0;

    private TypeElement getElement(Class<?> javaClass)
    {
        for (Element each : elems.getPackageElement(javaClass.getPackage().getName())
                .getEnclosedElements()) {
            if (each.getSimpleName().toString().equals(javaClass.getSimpleName()))
                return (TypeElement) each;
        }
        throw new AssertionError();
    }

    private Trees trees;
    private Elements elems;

    private File rdfDir = new File("rdf");

    private RDFParserFactory rdfaParserFactory;
    private RDFWriterFactory rdfWriterFactory;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        elems = processingEnv.getElementUtils();
        trees = Trees.instance(processingEnv);
        try {
            rdfaParserFactory = new RDFaParserFactory();
            rdfWriterFactory = new org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriterFactory();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        super.init(processingEnv);
    }
    
    Map<String, String> mappings = new TreeMap<String, String>() {{
        put("urn:org.ifcx.model#", "model");
        put(XMLSchema.NAMESPACE, "xsd");
    }};

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv)
    {
        if (!roundEnv.processingOver()) {
            Set<? extends Element> elements = roundEnv.getRootElements();
            for (Element each : elements) {
                if (each.getKind() == ElementKind.CLASS) {
                    try {
//                        Name pkgName = each.getEnclosingElement().getSimpleName();
                        Name pkgName = ((PackageElement) each.getEnclosingElement()).getQualifiedName();
//                        CharSequence relName = ((TypeElement) each).getQualifiedName() + ".html";
                        String fileName = pkgName.toString() + '.' + each.getSimpleName();
//                        FileObject outputFile = processingEnv.getFiler().createResource(rdfLocation, pkgName, relName, each);
//                        Writer writer = outputFile.openWriter();
                        File outputFile = new File(rdfDir, fileName + ".html");
                        Writer writer = new FileWriter(outputFile);
                        RDFaWriter rdFaWriter = new RDFaWriter(writer);

                        RDFaScanner visitor = new RDFaScanner(trees);

                        rdFaWriter.startRDF("java://" + ((TypeElement) each).getQualifiedName(), mappings);
                        rdFaWriter.startMeta();
                        rdFaWriter.endMeta();

                        TreePath path = trees.getPath(each);
                        JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit) path.getCompilationUnit();
                        JCTree.JCClassDecl klass = (JCTree.JCClassDecl) trees.getTree(each);

                        visitor.scan(unit, rdFaWriter);

                        rdFaWriter.endRDF();

                        writer.close();

/*
//                        FileObject rdfOutputFile = processingEnv.getFiler().createResource(rdfLocation, pkgName, each.getSimpleName() + ".rdf");
//                        RDFWriter rdfWriter = rdfWriterFactory.getWriter(rdfOutputFile.openWriter());
                        File rdfOutputFile = new File(rdfDir, fileName + ".rdf");
                        Writer rdfFileWriter = new FileWriter(rdfOutputFile);
                        RDFWriter rdfWriter = rdfWriterFactory.getWriter(rdfFileWriter);
//                        RDFParser rdfaParser = rdfaParserFactory.getParser();

                        RDFaParser rdfaParser = new RDFaParser();
                        rdfaParser.setDatatypeHandling(RDFParser.DatatypeHandling.IGNORE);
//                        File inputFile = new File(rdfaFileURI);
//                        Reader rdfaReader = new FileReader(inputFile);
//                        FileObject inputFile = processingEnv.getFiler().getResource(rdfLocation, pkgName, relName);
//                        Reader rdfaReader = inputFile.openReader(false);
                        Reader rdfaReader = new FileReader(outputFile);
                        rdfWriter.startRDF();
                        rdfaParser.setRDFHandler(rdfWriter);
                        rdfaParser.parse(rdfaReader, "file:/" + ((TypeElement) each).getQualifiedName());
                        rdfaReader.close();
                        rdfWriter.endRDF();
//                        rdfWriter.
                    } catch (RDFHandlerException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (RDFParseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (TransformerConfigurationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process ignoring kind " + each.getKind());
                }
            }
        }
        return false;
    }

    static class MyRDFaParser extends  RDFaParser
    {
        private Executor executor;

        private Transformer transformer;

        public MyRDFaParser()
                throws TransformerConfigurationException
        {
            executor = Executors.newSingleThreadExecutor();
            TransformerFactory transFact = TransformerFactory.newInstance();
            ClassLoader cl = RDFaParser.class.getClassLoader();
            transformer = transFact.newTransformer(new StreamSource(cl.getResourceAsStream("META-INF/transformations/RDFa2RDFXML.xsl")));
            transformer.setParameter("http://apache.org/xml/features/validation/schema", false);
            transformer.setParameter("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            transformer.setParameter("com.sun/apache.org/xml/features/validation/schema", false);
            transformer.setParameter("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            transformer.setParameter("http://xml.org/sax/features/validation", false);
            transformer.setParameter(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
//            transformer.setURIResolver(new Resolver());
        }

    }
}
