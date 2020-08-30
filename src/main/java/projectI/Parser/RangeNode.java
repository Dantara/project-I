package projectI.Parser;

import java.util.Objects;

public class RangeNode implements ASTNode {
    public final ExpressionNode from;
    public final ExpressionNode to;
    public final boolean reverse;

    public RangeNode(ExpressionNode from, ExpressionNode to, boolean reverse) {
        this.from = from;
        this.to = to;
        this.reverse = reverse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeNode rangeNode = (RangeNode) o;
        return reverse == rangeNode.reverse &&
                Objects.equals(from, rangeNode.from) &&
                Objects.equals(to, rangeNode.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, reverse);
    }
}
