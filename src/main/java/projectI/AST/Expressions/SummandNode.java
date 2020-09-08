package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SummandNode implements ASTNode {
    public final FactorNode factor;
    public final List<OperatorWithNode<MultiplicationOperator, FactorNode>> otherFactors = new ArrayList<>();

    public SummandNode(FactorNode factor) {
        this.factor = factor;
    }

    public SummandNode addFactor(MultiplicationOperator operator, FactorNode factor) {
        otherFactors.add(new OperatorWithNode<>(operator, factor));
        return this;
    }

    public SummandNode addFactor(MultiplicationOperator operator, FactorNode factor, CodePosition operatorPosition) {
        otherFactors.add(new OperatorWithNode<>(operator, factor, operatorPosition));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummandNode that = (SummandNode) o;
        if (!factor.equals(that.factor)) return false;
        if (otherFactors.size() != that.otherFactors.size()) return false;

        for (int index = 0; index < otherFactors.size(); index++) {
            if (!otherFactors.get(index).equals(that.otherFactors.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(factor, otherFactors);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(factor);
        builder.append(" ");

        for (var other : otherFactors) {
            builder.append(other.operator);
            builder.append(" ");
            builder.append(other.node);
            builder.append(" ");
        }

        return builder.toString();
    }

    public CodePosition getPosition() {
        return factor.getPosition();
    }

    @Override
    public boolean validate() {
        if (factor == null || !factor.validate())
            return false;

        for (var factor : otherFactors) {
            if (factor == null)
                return false;

            if (factor.operator == null || factor.node == null
                    || !factor.node.validate() || factor.operatorPosition == null)
                return false;
        }

        return true;
    }
}
