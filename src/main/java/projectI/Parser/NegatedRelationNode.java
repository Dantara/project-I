package projectI.Parser;

import java.util.Objects;

public class NegatedRelationNode implements RelationNode {
    public final RelationNode innerRelation;

    public NegatedRelationNode(RelationNode innerRelation) {
        this.innerRelation = innerRelation;
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
}
