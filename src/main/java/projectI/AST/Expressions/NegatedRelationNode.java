package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;

import java.util.Objects;

public class NegatedRelationNode implements RelationNode {
    public final RelationNode innerRelation;
    public final CodePosition startPosition;
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

    @Override
    public Object tryEvaluateConstant() {
        var innerValue = innerRelation.tryEvaluateConstant();
        if (innerValue == null) return null;
        if (innerValue instanceof Double) return null;
        if (innerValue instanceof Integer) {
            int integerValue = (Integer) innerValue;
            if (integerValue != 0 && integerValue != 1) return null;

            innerValue = integerValue != 0;
        }

        return !((Boolean) innerValue);
    }

    @Override
    public RuntimeType getType() {
        return new RuntimePrimitiveType(PrimitiveType.BOOLEAN);
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
