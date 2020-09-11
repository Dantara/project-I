package projectI.AST.Expressions;

import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionNode implements FactorNode {
    public final RelationNode relation;
    public final List<OperatorWithNode<LogicalOperator, RelationNode>> otherRelations = new ArrayList<>();

    /**
     * Find a position in the source code
     * @return a position
     */
    public CodePosition getPosition() {
        return relation.getPosition();
    }

    /**
     * A constructor for initializing objects of class ExpressionNode
     * @param relation is a relation of the expression
     */
    public ExpressionNode(RelationNode relation) {
        this.relation = relation;
    }

    /**
     * Add a relation to the list of relations of the expression
     * @param operator is a logical operator which combine added relation with other ones
     * @param relation is a relation to add
     * @return Expression Node itself
     */
    public ExpressionNode addRelation(LogicalOperator operator, RelationNode relation) {
        otherRelations.add(new OperatorWithNode<>(operator, relation));
        return this;
    }

    /**
     * Add a relation to the list of relations of the expression
     * @param operator is a logical operator which combine added relation with other ones
     * @param relation is a relation to add
     * @param position is a position in the source code
     * @return Expression Node itself
     */
    public ExpressionNode addRelation(LogicalOperator operator, RelationNode relation, CodePosition position) {
        otherRelations.add(new OperatorWithNode<>(operator, relation, position));
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
        ExpressionNode that = (ExpressionNode) o;
        if (!Objects.equals(relation, that.relation)) return false;
        if (otherRelations.size() != that.otherRelations.size()) return false;

        for (int index = 0; index < otherRelations.size(); index++) {
            if (!otherRelations.get(index).equals(that.otherRelations.get(index)))
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
        return Objects.hash(relation, otherRelations);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(relation);
        builder.append(" ");

        for (var other : otherRelations) {
            builder.append(other.operator);
            builder.append(" ");
            builder.append(other.node);
            builder.append(" ");
        }

        return builder.toString();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        if (relation == null || !relation.validate())
            return false;

        for (var relation : otherRelations) {
            if (relation == null)
                return false;

            if (relation.operator == null || relation.node == null
                    || !relation.node.validate() || relation.operatorPosition == null)
                return false;
        }

        return true;
    }
}
