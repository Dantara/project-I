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

    /**
     * A constructor for initializing objects of class RangeNode
     * @param from is a left boundary of the range
     * @param to is a right boundary of the range
     * @param reverse is a direction of changing variable in the range(true is reversed, false is direct one)
     */
    public RangeNode(ExpressionNode from, ExpressionNode to, boolean reverse) {
        this.from = from;
        this.to = to;
        this.reverse = reverse;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class RangeNode
     * @param from is a left boundary of the range
     * @param to is a right boundary of the range
     * @param reverse is a direction of changing variable in the range(true is reversed, false is direct one)
     * @param startPosition is a start position in the source code
     */
    public RangeNode(ExpressionNode from, ExpressionNode to, boolean reverse, CodePosition startPosition) {
        this.from = from;
        this.to = to;
        this.reverse = reverse;
        this.startPosition = startPosition;
    }

    /**
     * Check whether this object is equal to the passed one.
     * @param o the object to check the equality with
     * @return true if this object is equal to the passed one, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeNode rangeNode = (RangeNode) o;
        return reverse == rangeNode.reverse &&
                Objects.equals(from, rangeNode.from) &&
                Objects.equals(to, rangeNode.to);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, reverse);
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return from != null && from.validate() &&
                to != null && to.validate() &&
                startPosition != null;
    }
}
