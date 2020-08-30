package projectI.Parser;

import java.util.Objects;

public class BinaryRelationNode implements RelationNode {
    public final SimpleNode simple;
    public final Comparison comparison;
    public final SimpleNode otherSimple;

    public BinaryRelationNode(SimpleNode simple) {
        this.simple = simple;
        this.comparison = null;
        this.otherSimple = null;
    }

    public BinaryRelationNode(SimpleNode simple, Comparison comparison, SimpleNode otherSimple) {
        this.simple = simple;
        this.comparison = comparison;
        this.otherSimple = otherSimple;
    }

    public enum Comparison {
        LESS, LESS_EQUAL,
        GREATER, GREATER_EQUAL,
        EQUAL, NOT_EQUAL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryRelationNode that = (BinaryRelationNode) o;
        return Objects.equals(simple, that.simple) &&
                comparison == that.comparison &&
                Objects.equals(otherSimple, that.otherSimple);
    }

    @Override
    public int hashCode() {
        return Objects.hash(simple, comparison, otherSimple);
    }

    @Override
    public String toString() {
        if (comparison == null)
            return simple.toString();

        return simple + " " + comparison + " " + otherSimple;
    }
}
