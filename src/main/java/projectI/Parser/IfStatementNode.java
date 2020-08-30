package projectI.Parser;

import java.util.Objects;

public class IfStatementNode implements StatementNode {
    public final ExpressionNode condition;
    public final BodyNode body;
    public final BodyNode elseBody;

    public IfStatementNode(ExpressionNode condition, BodyNode body, BodyNode elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    public IfStatementNode(ExpressionNode condition, BodyNode body) {
        this.condition = condition;
        this.body = body;
        this.elseBody = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfStatementNode that = (IfStatementNode) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body) &&
                Objects.equals(elseBody, that.elseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body, elseBody);
    }
}
