package org.ifcx.extractor;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;

import org.ifcx.extractor.util.RDFaWriter;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RDFaScanner extends TreeScanner<Object, RDFaWriter>
{
    ValueFactory vf = new ValueFactoryImpl();
    String namespace = "urn:org.ifcx.model#";

    Set<URI> compilationUnit = new HashSet<URI>() { { add(vf.createURI(namespace, "CompilationUnit")); } };

//    Set<URI> classURI = new HashSet<URI>() { { add(vf.createURI(namespace, "Class")); } };
    URI classURI = vf.createURI(namespace, "Class");
    Set<URI> classURISet = new HashSet<URI>() { { add(classURI); } };

    Set<URI> methodURI = new HashSet<URI>() { { add(vf.createURI(namespace, "Method")); } };
    private URI nameURI = vf.createURI(namespace, "name");

    @Override
    public Object visitCompilationUnit(CompilationUnitTree node, RDFaWriter rdFaWriter)
    {
        try {
//            rdFaWriter.startNode(node.getPackageName().toString(), compilationUnit);
            BNode bNode = vf.createBNode();
            rdFaWriter.startBlankNode(1, bNode, compilationUnit);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getPackageName().toString()));
            super.visitCompilationUnit(node, rdFaWriter);
//            rdFaWriter.endNode(node.getPackageName().toString(), compilationUnit);
            rdFaWriter.endBlankNode(1, bNode, compilationUnit);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitClass(ClassTree node, RDFaWriter rdFaWriter)
    {
        try {
//            rdFaWriter.startNode(node.getSimpleName().toString(), classURI);
            BNode bNode = vf.createBNode();
            rdFaWriter.startBlankNode(1, bNode, classURISet);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getSimpleName().toString()));
            super.visitClass(node, rdFaWriter);
//            rdFaWriter.endNode(node.getSimpleName().toString(), classURI);
            rdFaWriter.endBlankNode(1, bNode, classURISet);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitMethod(MethodTree node, RDFaWriter rdFaWriter)
    {
        try {
            rdFaWriter.startNode(node.getName().toString(), methodURI);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getName().toString()));
            super.visitMethod(node, rdFaWriter);
            rdFaWriter.endNode(node.getName().toString(), methodURI);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    private class RDFaScannerIOException extends RuntimeException
    {
        public RDFaScannerIOException(IOException e)
        {
            super("IOException in RDFaScanner", e);
        }
    }
}
