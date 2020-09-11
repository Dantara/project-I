package projectI.AST;

import projectI.AST.Expressions.*;
import projectI.AST.Primary.*;

public final class ASTUtils {
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
        return toExpression(new IntegralLiteralNode(value));
    }

    public static ExpressionNode realExpression(double value) {
        return toExpression(new RealLiteralNode(value, null, null));
    }

    public static ExpressionNode booleanExpression(boolean value) {
        return toExpression(BooleanLiteralNode.create(value));
    }

    public static ExpressionNode toExpression(FactorNode factor) {
        return new ExpressionNode(toRelation(factor));
    }

    public static SummandNode toSummand(PrimaryNode primaryNode) {
        return new SummandNode(primaryNode);
    }

    private ASTUtils() { }
}
