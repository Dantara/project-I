package projectI.AST.Flow;

import projectI.AST.ASTNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.CodePosition;

import java.util.Objects;

public class RangeNode implements ASTNode {
    public final ExpressionNode from;
    public final ExpressionNode to;
    public final boolean reverse;
    public final CodePosition startPosition;

    public RangeNode(ExpressionNode from, ExpressionNode to, boolean reverse) {
        this.from = from;
        this.to = to;
        this.reverse = reverse;
        this.startPosition = null;
    }

    public RangeNode(ExpressionNode from, ExpressionNode to, boolean reverse, CodePosition startPosition) {
        this.from = from;
        this.to = to;
        this.reverse = reverse;
        this.startPosition = startPosition;
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
