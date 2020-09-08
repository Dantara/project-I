package projectI.AST.Expressions;

import projectI.CodePosition;

import java.util.Objects;

public class NegatedRelationNode implements RelationNode {
    public final RelationNode innerRelation;
    public final CodePosition startPosition;

    public NegatedRelationNode(RelationNode innerRelation) {
        this.innerRelation = innerRelation;
        this.startPosition = null;
    }

    public NegatedRelationNode(RelationNode innerRelation, CodePosition startPosition) {
        this.innerRelation = innerRelation;
        this.startPosition = startPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NegatedRelationNode that = (NegatedRelationNode) o;
        return Objects.equals(innerRelation, that.innerRelation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerRelation);
    }

    @Override
    public CodePosition getPosition() {
        return startPosition;
    }

    @Override
    public boolean validate() {
        return innerRelation != null && innerRelation.validate() && startPosition != null;
    }
}
