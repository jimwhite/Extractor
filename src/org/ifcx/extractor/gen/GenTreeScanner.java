package org.ifcx.extractor.gen;

public class GenTreeScanner extends org.ifcx.extractor.TreeScanner<java.lang.Object, org.ifcx.extractor.util.RDFaWriter>
{

public java.lang.Object visitMethod(com.sun.source.tree.MethodTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Method", tree.getKind());
// true false public abstract com.sun.source.tree.BlockTree com.sun.source.tree.MethodTree.getBody() interface com.sun.source.tree.BlockTree
   scan("Body", tree.getBody(), param);
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.MethodTree.getDefaultValue() interface com.sun.source.tree.Tree
   scan("DefaultValue", tree.getDefaultValue(), param);
// true false public abstract com.sun.source.tree.ModifiersTree com.sun.source.tree.MethodTree.getModifiers() interface com.sun.source.tree.ModifiersTree
   scan("Modifiers", tree.getModifiers(), param);
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.MethodTree.getName() interface javax.lang.model.element.Name
   scan("Name", tree.getName(), param);
// false true public abstract java.util.List com.sun.source.tree.MethodTree.getParameters() interface java.util.List
   scan("Parameters", tree.getParameters(), param);
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.MethodTree.getReturnType() interface com.sun.source.tree.Tree
   scan("ReturnType", tree.getReturnType(), param);
// false true public abstract java.util.List com.sun.source.tree.MethodTree.getThrows() interface java.util.List
   scan("Throws", tree.getThrows(), param);
// false true public abstract java.util.List com.sun.source.tree.MethodTree.getTypeParameters() interface java.util.List
   scan("TypeParameters", tree.getTypeParameters(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitAnnotation(com.sun.source.tree.AnnotationTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Annotation", tree.getKind());
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.AnnotationTree.getAnnotationType() interface com.sun.source.tree.Tree
   scan("AnnotationType", tree.getAnnotationType(), param);
// false true public abstract java.util.List com.sun.source.tree.AnnotationTree.getArguments() interface java.util.List
   scan("Arguments", tree.getArguments(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitClass(com.sun.source.tree.ClassTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Class", tree.getKind());
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.ClassTree.getExtendsClause() interface com.sun.source.tree.Tree
   scan("ExtendsClause", tree.getExtendsClause(), param);
// false true public abstract java.util.List com.sun.source.tree.ClassTree.getImplementsClause() interface java.util.List
   scan("ImplementsClause", tree.getImplementsClause(), param);
// false true public abstract java.util.List com.sun.source.tree.ClassTree.getMembers() interface java.util.List
   scan("Members", tree.getMembers(), param);
// true false public abstract com.sun.source.tree.ModifiersTree com.sun.source.tree.ClassTree.getModifiers() interface com.sun.source.tree.ModifiersTree
   scan("Modifiers", tree.getModifiers(), param);
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.ClassTree.getSimpleName() interface javax.lang.model.element.Name
   scan("SimpleName", tree.getSimpleName(), param);
// false true public abstract java.util.List com.sun.source.tree.ClassTree.getTypeParameters() interface java.util.List
   scan("TypeParameters", tree.getTypeParameters(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitDoWhileLoop(com.sun.source.tree.DoWhileLoopTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("DoWhileLoop", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.DoWhileLoopTree.getCondition() interface com.sun.source.tree.ExpressionTree
   scan("Condition", tree.getCondition(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.DoWhileLoopTree.getStatement() interface com.sun.source.tree.StatementTree
   scan("Statement", tree.getStatement(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitExpressionStatement(com.sun.source.tree.ExpressionStatementTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("ExpressionStatement", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ExpressionStatementTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitForLoop(com.sun.source.tree.ForLoopTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("ForLoop", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ForLoopTree.getCondition() interface com.sun.source.tree.ExpressionTree
   scan("Condition", tree.getCondition(), param);
// false true public abstract java.util.List com.sun.source.tree.ForLoopTree.getInitializer() interface java.util.List
   scan("Initializer", tree.getInitializer(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.ForLoopTree.getStatement() interface com.sun.source.tree.StatementTree
   scan("Statement", tree.getStatement(), param);
// false true public abstract java.util.List com.sun.source.tree.ForLoopTree.getUpdate() interface java.util.List
   scan("Update", tree.getUpdate(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitSwitch(com.sun.source.tree.SwitchTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Switch", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.SwitchTree.getCases() interface java.util.List
   scan("Cases", tree.getCases(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.SwitchTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitWhileLoop(com.sun.source.tree.WhileLoopTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("WhileLoop", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.WhileLoopTree.getCondition() interface com.sun.source.tree.ExpressionTree
   scan("Condition", tree.getCondition(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.WhileLoopTree.getStatement() interface com.sun.source.tree.StatementTree
   scan("Statement", tree.getStatement(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitEmptyStatement(com.sun.source.tree.EmptyStatementTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("EmptyStatement", tree.getKind());
   v.end();
*/   return null;
}

public java.lang.Object visitWildcard(com.sun.source.tree.WildcardTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Wildcard", tree.getKind());
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.WildcardTree.getBound() interface com.sun.source.tree.Tree
   scan("Bound", tree.getBound(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitBlock(com.sun.source.tree.BlockTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Block", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.BlockTree.getStatements() interface java.util.List
   scan("Statements", tree.getStatements(), param);
// false false public abstract boolean com.sun.source.tree.BlockTree.isStatic() boolean
   scan("Static", tree.isStatic(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitImport(com.sun.source.tree.ImportTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Import", tree.getKind());
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.ImportTree.getQualifiedIdentifier() interface com.sun.source.tree.Tree
   scan("QualifiedIdentifier", tree.getQualifiedIdentifier(), param);
// false false public abstract boolean com.sun.source.tree.ImportTree.isStatic() boolean
   scan("Static", tree.isStatic(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitModifiers(com.sun.source.tree.ModifiersTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Modifiers", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.ModifiersTree.getAnnotations() interface java.util.List
   scan("Annotations", tree.getAnnotations(), param);
// false false public abstract java.util.Set com.sun.source.tree.ModifiersTree.getFlags() interface java.util.Set
   scan("Flags", tree.getFlags(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitTypeParameter(com.sun.source.tree.TypeParameterTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("TypeParameter", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.TypeParameterTree.getBounds() interface java.util.List
   scan("Bounds", tree.getBounds(), param);
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.TypeParameterTree.getName() interface javax.lang.model.element.Name
   scan("Name", tree.getName(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitMethodInvocation(com.sun.source.tree.MethodInvocationTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("MethodInvocation", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.MethodInvocationTree.getArguments() interface java.util.List
   scan("Arguments", tree.getArguments(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.MethodInvocationTree.getMethodSelect() interface com.sun.source.tree.ExpressionTree
   scan("MethodSelect", tree.getMethodSelect(), param);
// false true public abstract java.util.List com.sun.source.tree.MethodInvocationTree.getTypeArguments() interface java.util.List
   scan("TypeArguments", tree.getTypeArguments(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitAssert(com.sun.source.tree.AssertTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Assert", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.AssertTree.getCondition() interface com.sun.source.tree.ExpressionTree
   scan("Condition", tree.getCondition(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.AssertTree.getDetail() interface com.sun.source.tree.ExpressionTree
   scan("Detail", tree.getDetail(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitAssignment(com.sun.source.tree.AssignmentTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Assignment", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.AssignmentTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.AssignmentTree.getVariable() interface com.sun.source.tree.ExpressionTree
   scan("Variable", tree.getVariable(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitCompoundAssignment(com.sun.source.tree.CompoundAssignmentTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("CompoundAssignment", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.CompoundAssignmentTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.CompoundAssignmentTree.getVariable() interface com.sun.source.tree.ExpressionTree
   scan("Variable", tree.getVariable(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitBinary(com.sun.source.tree.BinaryTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Binary", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.BinaryTree.getLeftOperand() interface com.sun.source.tree.ExpressionTree
   scan("LeftOperand", tree.getLeftOperand(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.BinaryTree.getRightOperand() interface com.sun.source.tree.ExpressionTree
   scan("RightOperand", tree.getRightOperand(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitBreak(com.sun.source.tree.BreakTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Break", tree.getKind());
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.BreakTree.getLabel() interface javax.lang.model.element.Name
   scan("Label", tree.getLabel(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitCase(com.sun.source.tree.CaseTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Case", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.CaseTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// false true public abstract java.util.List com.sun.source.tree.CaseTree.getStatements() interface java.util.List
   scan("Statements", tree.getStatements(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitCatch(com.sun.source.tree.CatchTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Catch", tree.getKind());
// true false public abstract com.sun.source.tree.BlockTree com.sun.source.tree.CatchTree.getBlock() interface com.sun.source.tree.BlockTree
   scan("Block", tree.getBlock(), param);
// true false public abstract com.sun.source.tree.VariableTree com.sun.source.tree.CatchTree.getParameter() interface com.sun.source.tree.VariableTree
   scan("Parameter", tree.getParameter(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitConditionalExpression(com.sun.source.tree.ConditionalExpressionTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("ConditionalExpression", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ConditionalExpressionTree.getCondition() interface com.sun.source.tree.ExpressionTree
   scan("Condition", tree.getCondition(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ConditionalExpressionTree.getFalseExpression() interface com.sun.source.tree.ExpressionTree
   scan("FalseExpression", tree.getFalseExpression(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ConditionalExpressionTree.getTrueExpression() interface com.sun.source.tree.ExpressionTree
   scan("TrueExpression", tree.getTrueExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitContinue(com.sun.source.tree.ContinueTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Continue", tree.getKind());
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.ContinueTree.getLabel() interface javax.lang.model.element.Name
   scan("Label", tree.getLabel(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitErroneous(com.sun.source.tree.ErroneousTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Erroneous", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.ErroneousTree.getErrorTrees() interface java.util.List
   scan("ErrorTrees", tree.getErrorTrees(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitEnhancedForLoop(com.sun.source.tree.EnhancedForLoopTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("EnhancedForLoop", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.EnhancedForLoopTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.EnhancedForLoopTree.getStatement() interface com.sun.source.tree.StatementTree
   scan("Statement", tree.getStatement(), param);
// true false public abstract com.sun.source.tree.VariableTree com.sun.source.tree.EnhancedForLoopTree.getVariable() interface com.sun.source.tree.VariableTree
   scan("Variable", tree.getVariable(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitIdentifier(com.sun.source.tree.IdentifierTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Identifier", tree.getKind());
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.IdentifierTree.getName() interface javax.lang.model.element.Name
   scan("Name", tree.getName(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitIf(com.sun.source.tree.IfTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("If", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.IfTree.getCondition() interface com.sun.source.tree.ExpressionTree
   scan("Condition", tree.getCondition(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.IfTree.getElseStatement() interface com.sun.source.tree.StatementTree
   scan("ElseStatement", tree.getElseStatement(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.IfTree.getThenStatement() interface com.sun.source.tree.StatementTree
   scan("ThenStatement", tree.getThenStatement(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitArrayAccess(com.sun.source.tree.ArrayAccessTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("ArrayAccess", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ArrayAccessTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ArrayAccessTree.getIndex() interface com.sun.source.tree.ExpressionTree
   scan("Index", tree.getIndex(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitLabeledStatement(com.sun.source.tree.LabeledStatementTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("LabeledStatement", tree.getKind());
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.LabeledStatementTree.getLabel() interface javax.lang.model.element.Name
   scan("Label", tree.getLabel(), param);
// true false public abstract com.sun.source.tree.StatementTree com.sun.source.tree.LabeledStatementTree.getStatement() interface com.sun.source.tree.StatementTree
   scan("Statement", tree.getStatement(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitLiteral(com.sun.source.tree.LiteralTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Literal", tree.getKind());
// false false public abstract java.lang.Object com.sun.source.tree.LiteralTree.getValue() class java.lang.Object
   scan("Value", tree.getValue(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitNewArray(com.sun.source.tree.NewArrayTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("NewArray", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.NewArrayTree.getDimensions() interface java.util.List
   scan("Dimensions", tree.getDimensions(), param);
// false true public abstract java.util.List com.sun.source.tree.NewArrayTree.getInitializers() interface java.util.List
   scan("Initializers", tree.getInitializers(), param);
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.NewArrayTree.getType() interface com.sun.source.tree.Tree
   scan("Type", tree.getType(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitNewClass(com.sun.source.tree.NewClassTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("NewClass", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.NewClassTree.getArguments() interface java.util.List
   scan("Arguments", tree.getArguments(), param);
// true false public abstract com.sun.source.tree.ClassTree com.sun.source.tree.NewClassTree.getClassBody() interface com.sun.source.tree.ClassTree
   scan("ClassBody", tree.getClassBody(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.NewClassTree.getEnclosingExpression() interface com.sun.source.tree.ExpressionTree
   scan("EnclosingExpression", tree.getEnclosingExpression(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.NewClassTree.getIdentifier() interface com.sun.source.tree.ExpressionTree
   scan("Identifier", tree.getIdentifier(), param);
// false true public abstract java.util.List com.sun.source.tree.NewClassTree.getTypeArguments() interface java.util.List
   scan("TypeArguments", tree.getTypeArguments(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitParenthesized(com.sun.source.tree.ParenthesizedTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Parenthesized", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ParenthesizedTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitReturn(com.sun.source.tree.ReturnTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Return", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ReturnTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitMemberSelect(com.sun.source.tree.MemberSelectTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("MemberSelect", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.MemberSelectTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.MemberSelectTree.getIdentifier() interface javax.lang.model.element.Name
   scan("Identifier", tree.getIdentifier(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitSynchronized(com.sun.source.tree.SynchronizedTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Synchronized", tree.getKind());
// true false public abstract com.sun.source.tree.BlockTree com.sun.source.tree.SynchronizedTree.getBlock() interface com.sun.source.tree.BlockTree
   scan("Block", tree.getBlock(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.SynchronizedTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitThrow(com.sun.source.tree.ThrowTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Throw", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.ThrowTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitCompilationUnit(com.sun.source.tree.CompilationUnitTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("CompilationUnit", tree.getKind());
// false true public abstract java.util.List com.sun.source.tree.CompilationUnitTree.getImports() interface java.util.List
   scan("Imports", tree.getImports(), param);
// false false public abstract com.sun.source.tree.LineMap com.sun.source.tree.CompilationUnitTree.getLineMap() interface com.sun.source.tree.LineMap
   scan("LineMap", tree.getLineMap(), param);
// false true public abstract java.util.List com.sun.source.tree.CompilationUnitTree.getPackageAnnotations() interface java.util.List
   scan("PackageAnnotations", tree.getPackageAnnotations(), param);
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.CompilationUnitTree.getPackageName() interface com.sun.source.tree.ExpressionTree
   scan("PackageName", tree.getPackageName(), param);
// false false public abstract javax.tools.JavaFileObject com.sun.source.tree.CompilationUnitTree.getSourceFile() interface javax.tools.JavaFileObject
   scan("SourceFile", tree.getSourceFile(), param);
// false true public abstract java.util.List com.sun.source.tree.CompilationUnitTree.getTypeDecls() interface java.util.List
   scan("TypeDecls", tree.getTypeDecls(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitTry(com.sun.source.tree.TryTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Try", tree.getKind());
// true false public abstract com.sun.source.tree.BlockTree com.sun.source.tree.TryTree.getBlock() interface com.sun.source.tree.BlockTree
   scan("Block", tree.getBlock(), param);
// false true public abstract java.util.List com.sun.source.tree.TryTree.getCatches() interface java.util.List
   scan("Catches", tree.getCatches(), param);
// true false public abstract com.sun.source.tree.BlockTree com.sun.source.tree.TryTree.getFinallyBlock() interface com.sun.source.tree.BlockTree
   scan("FinallyBlock", tree.getFinallyBlock(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitParameterizedType(com.sun.source.tree.ParameterizedTypeTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("ParameterizedType", tree.getKind());
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.ParameterizedTypeTree.getType() interface com.sun.source.tree.Tree
   scan("Type", tree.getType(), param);
// false true public abstract java.util.List com.sun.source.tree.ParameterizedTypeTree.getTypeArguments() interface java.util.List
   scan("TypeArguments", tree.getTypeArguments(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitArrayType(com.sun.source.tree.ArrayTypeTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("ArrayType", tree.getKind());
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.ArrayTypeTree.getType() interface com.sun.source.tree.Tree
   scan("Type", tree.getType(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitTypeCast(com.sun.source.tree.TypeCastTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("TypeCast", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.TypeCastTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.TypeCastTree.getType() interface com.sun.source.tree.Tree
   scan("Type", tree.getType(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitPrimitiveType(com.sun.source.tree.PrimitiveTypeTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("PrimitiveType", tree.getKind());
// false false public abstract javax.lang.model.type.TypeKind com.sun.source.tree.PrimitiveTypeTree.getPrimitiveTypeKind() class javax.lang.model.type.TypeKind
   scan("PrimitiveTypeKind", tree.getPrimitiveTypeKind(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitInstanceOf(com.sun.source.tree.InstanceOfTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("InstanceOf", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.InstanceOfTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.InstanceOfTree.getType() interface com.sun.source.tree.Tree
   scan("Type", tree.getType(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitUnary(com.sun.source.tree.UnaryTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Unary", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.UnaryTree.getExpression() interface com.sun.source.tree.ExpressionTree
   scan("Expression", tree.getExpression(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitVariable(com.sun.source.tree.VariableTree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Variable", tree.getKind());
// true false public abstract com.sun.source.tree.ExpressionTree com.sun.source.tree.VariableTree.getInitializer() interface com.sun.source.tree.ExpressionTree
   scan("Initializer", tree.getInitializer(), param);
// true false public abstract com.sun.source.tree.ModifiersTree com.sun.source.tree.VariableTree.getModifiers() interface com.sun.source.tree.ModifiersTree
   scan("Modifiers", tree.getModifiers(), param);
// false false public abstract javax.lang.model.element.Name com.sun.source.tree.VariableTree.getName() interface javax.lang.model.element.Name
   scan("Name", tree.getName(), param);
// true false public abstract com.sun.source.tree.Tree com.sun.source.tree.VariableTree.getType() interface com.sun.source.tree.Tree
   scan("Type", tree.getType(), param);
   v.end();
*/   return null;
}

public java.lang.Object visitOther(com.sun.source.tree.Tree tree, org.ifcx.extractor.util.RDFaWriter param)
{ /*
   Vertex v = beginVertex("Other", tree.getKind());
// false false public abstract java.lang.Object com.sun.source.tree.Tree.accept(com.sun.source.tree.TreeVisitor,java.lang.Object) class java.lang.Object
// false false public abstract com.sun.source.tree.Tree$Kind com.sun.source.tree.Tree.getKind() class com.sun.source.tree.Tree$Kind
   scan("Kind", tree.getKind(), param);
   v.end();
*/   return null;
}

}
