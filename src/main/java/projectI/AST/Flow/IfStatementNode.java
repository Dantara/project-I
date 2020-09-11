package projectI.AST.Flow;

import projectI.AST.Declarations.BodyNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Statements.StatementNode;
import projectI.CodePosition;

import java.util.Objects;

public class IfStatementNode implements StatementNode {
    public final ExpressionNode condition;
    public final BodyNode body;
    public final BodyNode elseBody;
    public final CodePosition startPosition;

    /**
     * A constructor for initializing objects of class
     * @param condition is a condition of the if statement
     * @param body is a part of code that will be run if the condition holds
     * @param elseBody is a part of code that will be run if the condition does not hold
     * @param startPosition is a start position in the source code
     */
    public IfStatementNode(ExpressionNode condition, BodyNode body, BodyNode elseBody, CodePosition startPosition) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
        this.startPosition = startPosition;
    }

    /**
     * A constructor for initializing objects of class
     * @param condition is a condition of the if statement
     * @param body is a part of code that will be run if the condition holds
     * @param elseBody is a part of code that will be run if the condition does not hold
     */
    public IfStatementNode(ExpressionNode condition, BodyNode body, BodyNode elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class
     * @param condition is a condition of the if statement
     * @param body is a part of code that will be run if the condition holds
     */
    public IfStatementNode(ExpressionNode condition, BodyNode body) {
        this.condition = condition;
        this.body = body;
        this.elseBody = null;
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
        IfStatementNode that = (IfStatementNode) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body) &&
                Objects.equals(elseBody, that.elseBody);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("if (");
        builder.append(condition);
        builder.append(") {\n\t");
        builder.append(body);
        builder.append("}");

        if (elseBody != null) {
            builder.append("else {\n\t");
            builder.append(elseBody);
            builder.append("}");
        }

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body, elseBody);
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
                (elseBody == null || elseBody.validate()) &&
                startPosition != null;
    }
}
