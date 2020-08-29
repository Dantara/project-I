package projectI.Parser;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleNode implements ASTNode{
    public final FactorNode factor;
    public final List<Pair<Operator, FactorNode>> otherFactors = new ArrayList<>();

    public SimpleNode(FactorNode factor) {
        this.factor = factor;
    }

    public enum Operator {
        MULTIPLICATION, DIVISION, MODULO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleNode that = (SimpleNode) o;
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
}
