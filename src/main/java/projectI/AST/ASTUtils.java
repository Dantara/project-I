package projectI.AST;

import projectI.AST.Expressions.*;
import projectI.AST.Primary.BooleanLiteralNode;
import projectI.AST.Primary.IntegralLiteralNode;
import projectI.AST.Primary.RealLiteralNode;

public final class ASTUtils {
    public static ExpressionNode toExpression(FactorNode factor) {
        return new ExpressionNode(toRelation(factor));
    }

    public static ExpressionNode toExpression(SimpleNode simple) {
        return new ExpressionNode(new BinaryRelationNode(simple));
    }

    public static RelationNode toRelation(FactorNode factor) {
        return new BinaryRelationNode(new SimpleNode(new SummandNode(factor)));
    }

    public static SimpleNode toSimple(FactorNode factor) {
        return new SimpleNode(new SummandNode(factor));
    }

    public static ExpressionNode integerExpression(int value) {
        return toSummand(new IntegralLiteralNode(value));
    }

    public static ExpressionNode realExpression(double value) {
        return toSummand(new RealLiteralNode(value));
    }

    public static ExpressionNode booleanExpression(boolean value) {
        return toSummand(BooleanLiteralNode.create(value));
    }

    public static ExpressionNode toSummand(FactorNode summand) {
        return new ExpressionNode(toRelation(summand));
    }

    private ASTUtils() { }
}
