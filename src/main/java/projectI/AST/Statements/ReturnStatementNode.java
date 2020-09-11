package projectI.AST.Statements;

import projectI.AST.Expressions.ExpressionNode;
import projectI.CodePosition;

import java.util.Objects;

public class ReturnStatementNode implements StatementNode {
    public final ExpressionNode expression;
    public final CodePosition startPosition;

    /**
     * A constructor for initializing objects of class ReturnStatementNode
     * @param expression is an expression to return
     * @param startPosition is a start position in the source code
     */
    public ReturnStatementNode(ExpressionNode expression, CodePosition startPosition) {
        this.expression = expression;
        this.startPosition = startPosition;
    }

    /**
     * A constructor for initializing objects of class ReturnStatementNode
     * @param expression is an expression to return
     */
    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class ReturnStatementNode
     * @param startPosition is a start position in the source code
     */
    public ReturnStatementNode(CodePosition startPosition) {
        this.expression = null;
        this.startPosition = startPosition;
    }

    /**
     * A constructor for initializing objects of class ReturnStatementNode
     */
    public ReturnStatementNode() {
        this.expression = null;
        this.startPosition = null;
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
        ReturnStatementNode that = (ReturnStatementNode) o;
        return Objects.equals(expression, that.expression);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        if (expression == null) return "return";

        return "return{" + expression + "}";
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
        return (expression == null || expression.validate()) &&
                startPosition != null;
    }
}
