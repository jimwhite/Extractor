package org.ifcx.extractor;

import com.sun.source.tree.*;

public class BodyChecker implements TreeVisitor<Boolean, Void> {
    public Boolean scan(Tree node, Void aVoid)
    {
        return (node == null) || node.accept(this, aVoid);
    }

    public Boolean scan(Iterable<? extends Tree> nodes, Void aVoid) {
        if (nodes != null) {
            for (Tree node : nodes) {
                if (!scan(node, aVoid)) return false;
            }
        }
        return true;
    }

    public Boolean visitBlock(BlockTree node, Void aVoid) {
        if (node != null && node.getStatements().size() > 1) return false;
        return scan(node.getStatements(), aVoid);
    }

    public Boolean visitCompilationUnit(CompilationUnitTree node, Void aVoid) {
        return false;
    }

    public Boolean visitImport(ImportTree node, Void aVoid) {
        return false;
    }

    public Boolean visitArrayAccess(ArrayAccessTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid) && scan(node.getIndex(), aVoid);
    }

    public Boolean visitClass(ClassTree node, Void aVoid) {
        return false;
    }

    public Boolean visitConditionalExpression(ConditionalExpressionTree node, Void aVoid) {
        return scan(node.getCondition(), aVoid)
                && scan(node.getFalseExpression(), aVoid)
                && scan(node.getTrueExpression(), aVoid);
    }

    public Boolean visitMethod(MethodTree node, Void aVoid) {
        return false;
    }

    public Boolean visitModifiers(ModifiersTree node, Void aVoid) {
        return scan(node.getAnnotations(), aVoid);
    }

    public Boolean visitNewArray(NewArrayTree node, Void aVoid) {
        return scan(node.getDimensions(), aVoid)
                && scan(node.getInitializers(), aVoid)
                && scan(node.getType(), aVoid);
    }

    public Boolean visitNewClass(NewClassTree node, Void aVoid) {
        return false && scan(node.getArguments(), aVoid)
                && scan(node.getClassBody(), aVoid)
                && scan(node.getEnclosingExpression(), aVoid)
                && scan(node.getIdentifier(), aVoid)
                && scan(node.getTypeArguments(), aVoid);
    }

    public Boolean visitParenthesized(ParenthesizedTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid);
    }

    public Boolean visitReturn(ReturnTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid);
    }

    public Boolean visitMemberSelect(MemberSelectTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid);
    }

    public Boolean visitEmptyStatement(EmptyStatementTree node, Void aVoid) {
        return true;
    }

    public Boolean visitVariable(VariableTree node, Void aVoid) {
        return false;
    }

    public Boolean visitDoWhileLoop(DoWhileLoopTree node, Void aVoid) {
        return false;
    }

    public Boolean visitWhileLoop(WhileLoopTree node, Void aVoid) {
        return false;
    }

    public Boolean visitWildcard(WildcardTree node, Void aVoid) {
        return false;
    }

    public Boolean visitForLoop(ForLoopTree node, Void aVoid) {
        return false;
    }

    public Boolean visitIdentifier(IdentifierTree node, Void aVoid) {
        return true;
    }

    public Boolean visitIf(IfTree node, Void aVoid) {
        return false;
    }

    public Boolean visitEnhancedForLoop(EnhancedForLoopTree node, Void aVoid) {
        return false;
    }

    public Boolean visitLabeledStatement(LabeledStatementTree node, Void aVoid) {
        return false;
    }

    public Boolean visitLiteral(LiteralTree node, Void aVoid) {
        return true;
    }

    public Boolean visitSwitch(SwitchTree node, Void aVoid) {
        return false;
    }

    public Boolean visitSynchronized(SynchronizedTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid) && scan(node.getBlock(), aVoid);
    }

    public Boolean visitCase(CaseTree node, Void aVoid) {
        return false;
    }

    public Boolean visitTry(TryTree node, Void aVoid) {
        return false;
    }

    public Boolean visitCatch(CatchTree node, Void aVoid) {
        return false;
    }

    public Boolean visitBreak(BreakTree node, Void aVoid) {
        return false;
    }

    public Boolean visitContinue(ContinueTree node, Void aVoid) {
        return false;
    }

    public Boolean visitThrow(ThrowTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid);
    }

    public Boolean visitAnnotation(AnnotationTree node, Void aVoid) {
        return scan(node.getAnnotationType(), aVoid) && scan(node.getArguments(), aVoid);
    }

    public Boolean visitMethodInvocation(MethodInvocationTree node, Void aVoid) {
        return false;
    }

    public Boolean visitAssert(AssertTree node, Void aVoid) {
        return false;
    }

    public Boolean visitAssignment(AssignmentTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid) && scan(node.getVariable(), aVoid);
    }

    public Boolean visitCompoundAssignment(CompoundAssignmentTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid) && scan(node.getVariable(), aVoid);
    }

    public Boolean visitBinary(BinaryTree node, Void aVoid) {
        return scan(node.getLeftOperand(), aVoid) && scan(node.getRightOperand(), aVoid);
    }

    public Boolean visitErroneous(ErroneousTree node, Void aVoid) {
        return false;
    }

    public Boolean visitExpressionStatement(ExpressionStatementTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid);
    }

    public Boolean visitOther(Tree node, Void aVoid) {
        return false;
    }

    public Boolean visitTypeParameter(TypeParameterTree node, Void aVoid) {
        return false;
    }

    public Boolean visitInstanceOf(InstanceOfTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid) && scan(node.getType(), aVoid);
    }

    public Boolean visitUnary(UnaryTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid);
    }

    public Boolean visitParameterizedType(ParameterizedTypeTree node, Void aVoid) {
        return false;
    }

    public Boolean visitArrayType(ArrayTypeTree node, Void aVoid) {
        return scan(node.getType(), aVoid);
    }

    public Boolean visitTypeCast(TypeCastTree node, Void aVoid) {
        return scan(node.getExpression(), aVoid) && scan(node.getType(), aVoid);
    }

    public Boolean visitPrimitiveType(PrimitiveTypeTree node, Void aVoid) {
        return true;
    }
}
