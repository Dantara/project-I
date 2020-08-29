package projectI.Parser;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FactorNode implements ASTNode {
    public final SummandNode summand;
    public final List<Pair<Operator, SummandNode>> otherSummands = new ArrayList<>();

    public FactorNode(SummandNode summand) {
        this.summand = summand;
    }

    public enum Operator {
        PLUS, MINUS
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactorNode that = (FactorNode) o;
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
}
