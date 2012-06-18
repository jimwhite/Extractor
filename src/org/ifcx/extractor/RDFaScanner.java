package org.ifcx.extractor;

import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import org.ifcx.extractor.util.RDFaWriter;

import org.openrdf.model.BNode;
import org.openrdf.model.BNodeFactory;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeFactoryImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RDFaScanner extends TreeScanner<Object, RDFaWriter>
{
    ValueFactory vf = new ValueFactoryImpl();
    String namespace = "urn:org.ifcx.model#";

    BNodeFactory bnf = new BNodeFactoryImpl();

    URI compilationUnit = vf.createURI(namespace, "CompilationUnit");
    Set<URI> compilationUnitSet = new HashSet<URI>() { { add(compilationUnit); } };

//    Set<URI> classURI = new HashSet<URI>() { { add(vf.createURI(namespace, "Class")); } };
    URI classURI = vf.createURI(namespace, "Class");
    Set<URI> classURISet = new HashSet<URI>() { { add(classURI); } };

    URI methodURI = vf.createURI(namespace, "Method");
    Set<URI> methodURISet = new HashSet<URI>() { { add(methodURI); } };

    private URI nameURI = vf.createURI(namespace, "name");

    private URI startPositionURI = vf.createURI(namespace, "startPosition");
    private URI endPositionURI = vf.createURI(namespace, "endPosition");
    private URI startLineURI = vf.createURI(namespace, "startLine");
    private URI endLineURI = vf.createURI(namespace, "endLine");
    private URI startColumnURI = vf.createURI(namespace, "startColumn");
    private URI endColumnURI = vf.createURI(namespace, "endColumn");

    private URI isStaticURI = vf.createURI(namespace, "isStatic");
    private URI qnameURI = vf.createURI(namespace, "QName");

    private URI uriIdentifier = vf.createURI(namespace, "Identifier");
    private URI uriImport = vf.createURI(namespace, "Import");
    private URI uriPrimitiveType = vf.createURI(namespace, "PrimitiveType");
    private URI uriTypeParameter = vf.createURI(namespace, "TypeParameter");
    private URI uriArrayType = vf.createURI(namespace, "ArrayType");
    private URI uriModifiers = vf.createURI(namespace, "Modifiers");
    private URI uriReturnType = vf.createURI(namespace, "ReturnType");
    private URI uriParameters = vf.createURI(namespace, "Parameters");
    private URI uriThrows = vf.createURI(namespace, "Throws");
    private URI uriBody = vf.createURI(namespace, "Body");
    private URI uriBinary = vf.createURI(namespace, "Binary");
    private Set<URI> binaryURISet = new HashSet<URI>() { { add(uriBinary);}};
    private URI uriTypeParameters = vf.createURI(namespace, "TypeParameters");
    URI uriVariable = vf.createURI(namespace, "Variable");

    private final Trees trees;
    private final SourcePositions sourcePositions;

    private CompilationUnitTree compilationUnitTree;

    RDFaScanner(Trees trees)
    {
        this.trees = trees;
        this.sourcePositions = trees.getSourcePositions();
    }

    @Override
    public Object visitCompilationUnit(CompilationUnitTree node, RDFaWriter rdFaWriter)
    {
        CompilationUnitTree savedCompilationUnitTree = compilationUnitTree;
        compilationUnitTree = node;

        try {
//            rdFaWriter.startNode(node.getPackageName().toString(), compilationUnit);
//            BNode bNode = vf.createBNode();
//            rdFaWriter.startBlankNode(1, bNode, compilationUnit);
//            rdFaWriter.openProperty(1, compilationUnit);
//            String nodeId = node.getPackageName().toString();
            String nodeId = node.getPackageName().toString();
            rdFaWriter.startNode(nodeId, compilationUnitSet);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(nodeId));
            writePosition(rdFaWriter, node);
            super.visitCompilationUnit(node, rdFaWriter);
            rdFaWriter.endNode(nodeId, compilationUnitSet);
//            rdFaWriter.closeProperty(1, compilationUnit);
//            rdFaWriter.endNode(node.getPackageName().toString(), compilationUnit);
//            rdFaWriter.endBlankNode(1, bNode, compilationUnit);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        } finally {
            compilationUnitTree = savedCompilationUnitTree;
        }
    }

    private void writePosition(RDFaWriter rdFaWriter, Tree node) throws IOException
    {
        long startPosition = sourcePositions.getStartPosition(compilationUnitTree, node);
        long endPosition = sourcePositions.getEndPosition(compilationUnitTree, node);
        LineMap lineMap = compilationUnitTree.getLineMap();

        rdFaWriter.handleLiteral(1, startPositionURI, vf.createLiteral(startPosition));
//        rdFaWriter.handleLiteral(1, endPositionURI, vf.createLiteral(endPosition));
//        rdFaWriter.handleLiteral(1, startLineURI, vf.createLiteral(lineMap.getLineNumber(startPosition)));
//        rdFaWriter.handleLiteral(1, startColumnURI, vf.createLiteral(lineMap.getColumnNumber(startPosition)));
//        rdFaWriter.handleLiteral(1, endLineURI, vf.createLiteral(lineMap.getLineNumber(endPosition)));
//        rdFaWriter.handleLiteral(1, endColumnURI, vf.createLiteral(lineMap.getColumnNumber(endPosition)));
    }

    private void writeSpan(RDFaWriter rdFaWriter, Tree node) throws IOException
    {
        long startPosition = sourcePositions.getStartPosition(compilationUnitTree, node);
        long endPosition = sourcePositions.getEndPosition(compilationUnitTree, node);
//        LineMap lineMap = compilationUnitTree.getLineMap();

        rdFaWriter.handleLiteral(1, startPositionURI, vf.createLiteral(startPosition));
        rdFaWriter.handleLiteral(1, endPositionURI, vf.createLiteral(endPosition));
//        rdFaWriter.handleLiteral(1, startLineURI, vf.createLiteral(lineMap.getLineNumber(startPosition)));
//        rdFaWriter.handleLiteral(1, startColumnURI, vf.createLiteral(lineMap.getColumnNumber(startPosition)));
//        rdFaWriter.handleLiteral(1, endLineURI, vf.createLiteral(lineMap.getLineNumber(endPosition)));
//        rdFaWriter.handleLiteral(1, endColumnURI, vf.createLiteral(lineMap.getColumnNumber(endPosition)));
    }

    @Override
    public Object visitClass(ClassTree node, RDFaWriter rdFaWriter)
    {
        try {
//            rdFaWriter.startNode(node.getSimpleName().toString(), classURI);
//            BNode bNode = vf.createBNode();
//            rdFaWriter.startBlankNode(1, bNode, classURISet);
//            rdFaWriter.openProperty(1, classURI);
//            TreePath path = TreePath.getPath(compilationUnitTree, node);
            String nodeId = compilationUnitTree.getPackageName() + node.getSimpleName().toString();
//            String nodeId = "foo";
            if (nodeId.length() < 1) {
                System.err.println("Empty string for class name");
            }
            if (nodeId.contains("$")) {
                System.err.println("Inner class name: " + nodeId);
            }
            Symbol.ClassSymbol sym = ((JCTree.JCClassDecl) node).sym;
            if (sym != null) {
//                nodeId = sym.getQualifiedName().toString();
                nodeId = sym.className();
            } else {
                System.err.println("Null sym in visitClass " + nodeId);
            }

            if (nodeId.length() < 1) {
                System.err.println("Empty string for class name");
            }
            if (nodeId.contains("$")) {
                System.err.println("Inner class name: " + nodeId);
            }

//            if (node instanceof Element) {
//                TreePath path = trees.getPath((Element) node);
//                Scope scope = trees.getScope(path);
//                if (scope != null) {
//                    nodeId = scope.getEnclosingClass().getQualifiedName() + nodeId;
//                }
//            }

            rdFaWriter.startNode(nodeId, classURISet);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(nodeId));
            writePosition(rdFaWriter, node);
            if (sym != null) {
                rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(sym.getKind().toString()));
                rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(sym.getNestingKind().toString()));
            }
            super.visitClass(node, rdFaWriter);
            rdFaWriter.endNode(nodeId, classURISet);
//            rdFaWriter.closeProperty(1, classURI);
//            rdFaWriter.endNode(node.getSimpleName().toString(), classURI);
//            rdFaWriter.endBlankNode(1, bNode, classURISet);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitMethod(MethodTree node, RDFaWriter rdFaWriter)
    {
        try {
//            rdFaWriter.openProperty(1, methodURI);
            String name = node.getName().toString();
            String uriName = name.replace("%", "%25").replace("<", "%60").replace(">", "%62");

            rdFaWriter.startNode(uriName, methodURISet);

            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(name));
            writePosition(rdFaWriter, node);

            scan(node.getModifiers(), rdFaWriter);

            rdFaWriter.openProperty(1, uriReturnType);
            scan(node.getReturnType(), rdFaWriter);
            rdFaWriter.closeProperty(1, uriReturnType);

            scan(node.getTypeParameters(), rdFaWriter);

            rdFaWriter.openProperty(1, uriParameters);
            scan(node.getParameters(), rdFaWriter);
            rdFaWriter.closeProperty(1, uriParameters);

//            if (node.getThrows() != null) {
                rdFaWriter.openProperty(1, uriThrows);
                scan(node.getThrows(), rdFaWriter);
                rdFaWriter.closeProperty(1, uriThrows);
//            }

            rdFaWriter.openProperty(1, uriBody);
            writePosition(rdFaWriter, node.getBody());
            scan(node.getBody(), rdFaWriter);
            rdFaWriter.closeProperty(1, uriBody);

            rdFaWriter.endNode(uriName, methodURISet);
//            rdFaWriter.closeProperty(1, methodURI);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitIdentifier(IdentifierTree node, RDFaWriter rdFaWriter)
    {
        try {
            rdFaWriter.openProperty(1, uriIdentifier);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getName().toString()));
            writePosition(rdFaWriter, node);
            super.visitIdentifier(node, rdFaWriter);
            rdFaWriter.closeProperty(1, uriIdentifier);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitImport(ImportTree node,  RDFaWriter rdFaWriter) {
        try {
            rdFaWriter.openProperty(1, uriImport);
            writeSpan(rdFaWriter, node.getQualifiedIdentifier());
            rdFaWriter.handleLiteral(1, isStaticURI, vf.createLiteral(node.isStatic()));
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getQualifiedIdentifier().toString(), qnameURI));
//            super.visitImport(node, rdFaWriter);
            rdFaWriter.closeProperty(1, uriImport);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }


    @Override
    public Object visitPrimitiveType(PrimitiveTypeTree node, RDFaWriter rdFaWriter)
    {
        try {
            rdFaWriter.openProperty(1, uriPrimitiveType);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getPrimitiveTypeKind().toString()));
            writePosition(rdFaWriter, node);
            super.visitPrimitiveType(node, rdFaWriter);
            rdFaWriter.closeProperty(1, uriPrimitiveType);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitParameterizedType(ParameterizedTypeTree node, RDFaWriter rdFaWriter)
    {
        return super.visitParameterizedType(node, rdFaWriter);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Object visitTypeParameter(TypeParameterTree node, RDFaWriter rdFaWriter)
    {
        return super.visitTypeParameter(node, rdFaWriter);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Object visitVariable(VariableTree node, RDFaWriter rdFaWriter)
    {
        try {
            rdFaWriter.openProperty(1, uriVariable);
            rdFaWriter.handleLiteral(1, nameURI, vf.createLiteral(node.getName().toString()));
            writePosition(rdFaWriter, node);
            super.visitVariable(node, rdFaWriter);
            rdFaWriter.closeProperty(1, uriVariable);

            return null;
        } catch (IOException e) {
            throw new RDFaScannerIOException(e);
        }
    }

    @Override
    public Object visitModifiers(ModifiersTree node, RDFaWriter rdFaWriter)
    {
        return super.visitModifiers(node, rdFaWriter);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Object visitArrayType(ArrayTypeTree node, RDFaWriter rdFaWriter)
    {
        return super.visitArrayType(node, rdFaWriter);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Object visitBinary(BinaryTree node, RDFaWriter rdFaWriter)
    {
        try {
//            rdFaWriter.openProperty(1, uriBinary);
            BNode bn = bnf.createBNode();
            rdFaWriter.startBlankNode(1, bn, binaryURISet);
            writePosition(rdFaWriter, node);
            super.visitBinary(node, rdFaWriter);
            rdFaWriter.endBlankNode(1, bn, binaryURISet);
//            rdFaWriter.closeProperty(1, uriBinary);

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
