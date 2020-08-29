package projectI.Parser;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModifiablePrimaryNode implements PrimaryNode {
    public final IdentifierNode identifier;
    public final List<Pair<IdentifierNode, ExpressionNode>> accessors = new ArrayList<>();

    public ModifiablePrimaryNode(IdentifierNode identifier) {
        this.identifier = identifier;
    }

    public ModifiablePrimaryNode addMember(IdentifierNode identifier) {
        accessors.add(new Pair<>(identifier, null));
        return this;
    }

    public ModifiablePrimaryNode addIndexer(ExpressionNode expression) {
        accessors.add(new Pair<>(null, expression));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifiablePrimaryNode that = (ModifiablePrimaryNode) o;
        if (!identifier.equals(that.identifier)) return false;
        if (accessors.size() != that.accessors.size()) return false;

        for (int index = 0; index < accessors.size(); index++) {
            if (!accessors.get(index).equals(that.accessors.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, accessors);
    }
}
