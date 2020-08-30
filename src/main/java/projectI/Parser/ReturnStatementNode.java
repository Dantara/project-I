package projectI.Parser;

import java.util.Objects;

public class ReturnStatementNode implements StatementNode {
    public final ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ReturnStatementNode() {
        this.expression = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnStatementNode that = (ReturnStatementNode) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
