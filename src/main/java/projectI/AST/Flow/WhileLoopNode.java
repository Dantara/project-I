package projectI.AST.Flow;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.BodyNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Statements.StatementNode;
import projectI.CodePosition;

import java.util.Objects;

public class WhileLoopNode implements StatementNode {
    public final ExpressionNode condition;
    public final BodyNode body;
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
     * A constructor for initializing objects of class WhileLoopNode
     * @param condition is a condition of the while loop
     * @param body is a part of code that will be executed if the condition holds
     */
    public WhileLoopNode(ExpressionNode condition, BodyNode body) {
        this.condition = condition;
        this.body = body;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class WhileLoopNode
     * @param condition is a condition of the while loop
     * @param body is a part of code that will be executed if the condition holds
     * @param startPosition is a start position in the source code
     */
    public WhileLoopNode(ExpressionNode condition, BodyNode body, CodePosition startPosition) {
        this.condition = condition;
        this.body = body;
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
        WhileLoopNode that = (WhileLoopNode) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }

    /**
     * Find a start position in the source code
     * @return the position
     */
    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return condition != null && condition.validate() &&
                body != null && body.validate() &&
                startPosition != null;
    }

    @Override
    public String toString() {
        return "while " + condition + " loop {" + body + "}";
    }
}
