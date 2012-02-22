package org.ifcx.extractor;


import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import org.ifcx.extractor.util.RDFaWriter;
import org.openrdf.model.vocabulary.XMLSchema;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RDFExtractor extends AbstractProcessor
{
    private int tally = 0;

    private class Inliner extends TreeTranslator
    {

        @Override
        public void visitAssert(JCTree.JCAssert tree)
        {
            super.visitAssert(tree);
            JCTree.JCStatement newNode = makeIfAssertionThrowException(tree);
            //System.out.println(newNode);
            tally++;
            result = newNode;
        }

        private JCTree.JCStatement makeIfAssertionThrowException(JCTree.JCAssert node)
        {
            // if (!(%%cond%%) throw new AssertionError(%%detail%%);
            List<JCTree.JCExpression> args = node.getDetail() == null ? List
                    .<JCTree.JCExpression>nil() : List.of(node.detail);
            JCTree.JCExpression expr = make.NewClass(null, null, make
                    .Ident((Symbol) getElement(AssertionError.class)), args, null);
            return make.If(make.Unary(JCTree.NOT, node.cond), make.Throw(expr), null);
        }

    }

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
    private TreeMaker make;
    private Elements elems;

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

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv)
    {
        if (!roundEnv.processingOver()) {
            Set<? extends Element> elements = roundEnv.getRootElements();
            for (Element each : elements) {
                if (each.getKind() == ElementKind.CLASS) {
                    try {
                        Name pkgName = each.getEnclosingElement().getSimpleName();
                        CharSequence relName = ((TypeElement) each).getQualifiedName() + ".html";
                        FileObject outputFile = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, pkgName, relName, each);
                        RDFaWriter writer = new RDFaWriter(outputFile.openWriter());

                        RDFaScanner visitor = new RDFaScanner();

                        writer.startRDF("java://" + ((TypeElement) each).getQualifiedName(), mappings);
                        writer.startMeta();
                        writer.endMeta();

                        TreePath path = trees.getPath(each);
                        JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit) path.getCompilationUnit();
                        JCTree.JCClassDecl klass = (JCTree.JCClassDecl) trees.getTree(each);

                        visitor.scan(unit, writer);

                        writer.endRDF();

                        writer.close();
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
}
