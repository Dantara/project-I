package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;

import java.util.Objects;

public class BinaryRelationNode implements RelationNode {
    public final SimpleNode simple;
    public final Comparison comparison;
    public final SimpleNode otherSimple;
    public final CodePosition comparisonPosition;
    public ASTNode parent;

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    /**
     * A constructor for initializing objects of class BinaryRelationNode
     * @param simple is an element of relation
     */
    public BinaryRelationNode(SimpleNode simple) {
        this.simple = simple;
        this.comparison = null;
        this.otherSimple = null;
        this.comparisonPosition = null;
    }

    /**
     * A constructor for initializing objects of class BinaryRelationNode
     * @param simple is an element of the relation
     * @param comparison is a comparison sign of the relation
     * @param otherSimple is another element of the relation
     */
    public BinaryRelationNode(SimpleNode simple, Comparison comparison, SimpleNode otherSimple) {
        this.simple = simple;
        this.comparison = comparison;
        this.otherSimple = otherSimple;
        this.comparisonPosition = null;
    }

    /**
     * A constructor for initializing objects of class BinaryRelationNode
     * @param simple is an element of the relation
     * @param comparison is a comparison sign of the relation
     * @param otherSimple is another element of the relation
     * @param comparisonPosition is a position of the comparison sign
     */
    public BinaryRelationNode(SimpleNode simple, Comparison comparison, SimpleNode otherSimple, CodePosition comparisonPosition) {
        this.simple = simple;
        this.comparison = comparison;
        this.otherSimple = otherSimple;
        this.comparisonPosition = comparisonPosition;
    }

    /**
     * Find a position in the source code
     * @return the position
     */
    @Override
    public CodePosition getPosition() {
        return simple.getPosition();
    }

    @Override
    public Object tryEvaluateConstant() {
        var leftValue = simple.tryEvaluateConstant();
        if (leftValue == null) return null;
        if (comparison == null) return leftValue;

        var rightValue = simple.tryEvaluateConstant();
        if (rightValue == null) return null;

        if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
            return switch (comparison) {
                case EQUAL -> leftValue == rightValue;
                case NOT_EQUAL -> leftValue != rightValue;
                default -> null;
            };
        }

        if (leftValue instanceof Double && rightValue instanceof Integer) {
            rightValue = Double.valueOf((Integer) rightValue);
        }

        if (leftValue instanceof Integer && rightValue instanceof Double) {
            leftValue = Double.valueOf((Integer) leftValue);
        }

        if (leftValue instanceof Integer && rightValue instanceof Integer) {
            return switch (comparison) {
                case LESS -> (Integer) leftValue < (Integer) rightValue;
                case LESS_EQUAL -> (Integer) leftValue <= (Integer) rightValue;
                case GREATER -> (Integer) leftValue > (Integer) rightValue;
                case GREATER_EQUAL -> (Integer) leftValue >= (Integer) rightValue;
                case EQUAL -> leftValue == rightValue;
                case NOT_EQUAL -> leftValue != rightValue;
            };
        }

        if (leftValue instanceof Double && rightValue instanceof Double) {
            return switch (comparison) {
                case LESS -> (Double) leftValue < (Double) rightValue;
                case LESS_EQUAL -> (Double) leftValue <= (Double) rightValue;
                case GREATER -> (Double) leftValue > (Double) rightValue;
                case GREATER_EQUAL -> (Double) leftValue >= (Double) rightValue;
                case EQUAL -> leftValue == rightValue;
                case NOT_EQUAL -> leftValue != rightValue;
            };
        }

        return null;
    }

    @Override
    public RuntimeType getType() {
        if (comparison == null) return simple.getType();

        return new RuntimePrimitiveType(PrimitiveType.BOOLEAN);
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return simple != null && simple.validate() &&
                (comparison == null && otherSimple == null && comparisonPosition == null ||
                        comparison != null && otherSimple != null && otherSimple.validate() && comparisonPosition != null);
    }

    /**
     * List of possible comparison signs
     */
    public enum Comparison {
        LESS, LESS_EQUAL,
        GREATER, GREATER_EQUAL,
        EQUAL, NOT_EQUAL
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
        BinaryRelationNode that = (BinaryRelationNode) o;
        return Objects.equals(simple, that.simple) &&
                comparison == that.comparison &&
                Objects.equals(otherSimple, that.otherSimple);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(simple, comparison, otherSimple);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        if (comparison == null)
            return simple.toString();

        return simple + " " + comparison + " " + otherSimple;
    }
}
