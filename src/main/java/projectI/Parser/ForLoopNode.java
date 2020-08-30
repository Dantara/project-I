package projectI.Parser;

import java.util.Objects;

public class ForLoopNode implements StatementNode {
    public final IdentifierNode variable;
    public final RangeNode range;
    public final BodyNode body;

    public ForLoopNode(IdentifierNode variable, RangeNode range, BodyNode body) {
        this.variable = variable;
        this.range = range;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForLoopNode that = (ForLoopNode) o;
        return Objects.equals(variable, that.variable) &&
                Objects.equals(range, that.range) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, range, body);
    }
}
