package projectI.AST.Expressions;

import org.javatuples.Pair;
import projectI.AST.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SummandNode implements ASTNode {
    public final FactorNode factor;
    public final List<Pair<MultiplicationOperator, FactorNode>> otherFactors = new ArrayList<>();

    public SummandNode(FactorNode factor) {
        this.factor = factor;
    }

    public SummandNode addFactor(MultiplicationOperator operator, FactorNode factor) {
        otherFactors.add(new Pair<>(operator, factor));
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
            builder.append(other.getValue0());
            builder.append(" ");
            builder.append(other.getValue1());
            builder.append(" ");
        }

        return builder.toString();
    }
}
