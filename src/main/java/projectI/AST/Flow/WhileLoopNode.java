package projectI.AST.Flow;

import projectI.AST.Declarations.BodyNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Statements.StatementNode;
import projectI.CodePosition;

import java.util.Objects;

public class WhileLoopNode implements StatementNode {
    public final ExpressionNode condition;
    public final BodyNode body;
    public final CodePosition startPosition;

    public WhileLoopNode(ExpressionNode condition, BodyNode body) {
        this.condition = condition;
        this.body = body;
        this.startPosition = null;
    }

    public WhileLoopNode(ExpressionNode condition, BodyNode body, CodePosition startPosition) {
        this.condition = condition;
        this.body = body;
        this.startPosition = startPosition;
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
