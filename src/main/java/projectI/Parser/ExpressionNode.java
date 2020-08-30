package projectI.Parser;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionNode implements SummandNode {
    public final RelationNode relation;
    public final List<Pair<Operator, RelationNode>> otherRelations = new ArrayList<>();

    public ExpressionNode(RelationNode relation) {
        this.relation = relation;
    }

    public enum Operator {
        AND, OR, XOR
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionNode that = (ExpressionNode) o;
        if (!Objects.equals(relation, that.relation)) return false;
        if (otherRelations.size() != that.otherRelations.size()) return false;

        for (int index = 0; index < otherRelations.size(); index++) {
            if (!otherRelations.get(index).equals(that.otherRelations.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, otherRelations);
    }

    public static ExpressionNode integerLiteral(int value) {
        return summand(new IntegralLiteralNode(value));
    }

    public static ExpressionNode realLiteral(double value) {
        return summand(new RealLiteralNode(value));
    }

    public static ExpressionNode booleanLiteral(boolean value) {
        return summand(new BooleanLiteralNode(value));
    }

    private static ExpressionNode summand(SummandNode summand) {
        return new ExpressionNode(new BinaryRelationNode(new SimpleNode(new FactorNode(summand))));
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(relation);
        builder.append(" ");

        for (var other : otherRelations) {
            builder.append(other.getValue0());
            builder.append(" ");
            builder.append(other.getValue1());
            builder.append(" ");
        }

        return builder.toString();
    }
}
