package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleNode implements ASTNode {
    public final SummandNode summand;
    public final List<OperatorWithNode<AdditionOperator, SummandNode>> otherSummands = new ArrayList<>();

    public SimpleNode(SummandNode summand) {
        this.summand = summand;
    }

    public SimpleNode addSummand(AdditionOperator operator, SummandNode summand) {
        otherSummands.add(new OperatorWithNode<>(operator, summand));
        return this;
    }

    public SimpleNode addSummand(AdditionOperator operator, SummandNode summand, CodePosition operatorPosition) {
        otherSummands.add(new OperatorWithNode<>(operator, summand, operatorPosition));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleNode that = (SimpleNode) o;
        if (!summand.equals(that.summand)) return false;
        if (otherSummands.size() != that.otherSummands.size()) return false;

        for (int index = 0; index < otherSummands.size(); index++) {
            if (!otherSummands.get(index).equals(that.otherSummands.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(summand, otherSummands);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(summand);
        builder.append(" ");

        for (var other : otherSummands) {
            builder.append(other.operator);
            builder.append(" ");
            builder.append(other.node);
            builder.append(" ");
        }

        return builder.toString();
    }

    public CodePosition getPosition() {
        return summand.getPosition();
    }
}
