package projectI.AST.Statements;

import projectI.AST.Expressions.ExpressionNode;
import projectI.CodePosition;

import java.util.Objects;

public class ReturnStatementNode implements StatementNode {
    public final ExpressionNode expression;
    public final CodePosition startPosition;

    public ReturnStatementNode(ExpressionNode expression, CodePosition startPosition) {
        this.expression = expression;
        this.startPosition = startPosition;
    }

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
        this.startPosition = null;
    }

    public ReturnStatementNode(CodePosition startPosition) {
        this.expression = null;
        this.startPosition = startPosition;
    }

    public ReturnStatementNode() {
        this.expression = null;
        this.startPosition = null;
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

    @Override
    public String toString() {
        if (expression == null) return "return";

        return "return{" + expression + "}";
    }
}
