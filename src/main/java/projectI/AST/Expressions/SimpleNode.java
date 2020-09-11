package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleNode implements ASTNode {
    public final SummandNode summand;
    public final List<OperatorWithNode<AdditionOperator, SummandNode>> otherSummands = new ArrayList<>();

    /**
     * A constructor for initializing objects of class SimpleNode
     * @param summand is a summand to add/subtract in the simple
     */
    public SimpleNode(SummandNode summand) {
        this.summand = summand;
    }

    /**
     * Add a summand to the list of summands with its operator
     * @param operator is a sign of the added summand
     * @param summand is an added term to the simple
     * @return Simple Node itself
     */
    public SimpleNode addSummand(AdditionOperator operator, SummandNode summand) {
        otherSummands.add(new OperatorWithNode<>(operator, summand));
        return this;
    }

    /**
     * Add a summand to the list of summands with its operator
     * @param operator is a sign of the added summand
     * @param summand is an added term to the simple
     * @param operatorPosition is a position of the operator in the source code
     * @return Simple Node itself
     */
    public SimpleNode addSummand(AdditionOperator operator, SummandNode summand, CodePosition operatorPosition) {
        otherSummands.add(new OperatorWithNode<>(operator, summand, operatorPosition));
        return this;
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
        SimpleNode that = (SimpleNode) o;
        if (!summand.equals(that.summand)) return false;
        if (otherSummands.size() != that.otherSummands.size()) return false;

        for (int index = 0; index < otherSummands.size(); index++) {
            if (!otherSummands.get(index).equals(that.otherSummands.get(index)))
                return false;
        }

        return true;
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(summand, otherSummands);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
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

    /**
     * Find a position in the source code
     * @return the position
     */
    public CodePosition getPosition() {
        return summand.getPosition();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        if (summand == null || !summand.validate())
            return false;

        for (var summands : otherSummands) {
            if (summands == null)
                return false;

            if (summands.operator == null || summands.node == null
                    || !summands.node.validate() || summands.operatorPosition == null)
                return false;
        }

        return true;
    }
}
