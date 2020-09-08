package projectI.AST.Expressions;

import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionNode implements FactorNode {
    public final RelationNode relation;
    public final List<OperatorWithNode<LogicalOperator, RelationNode>> otherRelations = new ArrayList<>();

    public CodePosition getPosition() {
        return relation.getPosition();
    }

    public ExpressionNode(RelationNode relation) {
        this.relation = relation;
    }

    public ExpressionNode addRelation(LogicalOperator operator, RelationNode relation) {
        otherRelations.add(new OperatorWithNode<>(operator, relation));
        return this;
    }

    public ExpressionNode addRelation(LogicalOperator operator, RelationNode relation, CodePosition position) {
        otherRelations.add(new OperatorWithNode<>(operator, relation, position));
        return this;
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

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(relation);
        builder.append(" ");

        for (var other : otherRelations) {
            builder.append(other.operator);
            builder.append(" ");
            builder.append(other.node);
            builder.append(" ");
        }

        return builder.toString();
    }
}
