package projectI.Parser;

import java.util.Objects;

public class WhileLoopNode implements StatementNode {
    public final ExpressionNode condition;
    public final BodyNode body;

    public WhileLoopNode(ExpressionNode condition, BodyNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileLoopNode that = (WhileLoopNode) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }
}
