package projectI.AST.Expressions;

import projectI.CodePosition;

import java.util.Objects;

public class NegatedRelationNode implements RelationNode {
    public final RelationNode innerRelation;
    public final CodePosition startPosition;

    /**
     * A constructor for initializing objects of class BinaryRelationNode
     * @param innerRelation is an inner relation node for negating
     */
    public NegatedRelationNode(RelationNode innerRelation) {
        this.innerRelation = innerRelation;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class BinaryRelationNode
     * @param innerRelation is an inner relation node for negating
     * @param startPosition is a start position in the source code
     */
    public NegatedRelationNode(RelationNode innerRelation, CodePosition startPosition) {
        this.innerRelation = innerRelation;
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
        NegatedRelationNode that = (NegatedRelationNode) o;
        return Objects.equals(innerRelation, that.innerRelation);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(innerRelation);
    }

    /**
     * Find a position in the source code
     * @return the position
     */
    @Override
    public CodePosition getPosition() {
        return startPosition;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return innerRelation != null && innerRelation.validate() && startPosition != null;
    }
}
