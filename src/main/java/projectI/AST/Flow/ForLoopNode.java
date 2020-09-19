package projectI.AST.Flow;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.BodyNode;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Statements.StatementNode;
import projectI.CodePosition;

import java.util.Objects;

public class ForLoopNode implements StatementNode {
    public final IdentifierNode variable;
    public final RangeNode range;
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
     * A constructor for initializing objects of class ForLoopNode
     * @param variable is an identifier that will be changed each iteration of the loop
     * @param range is an interval in which variable will change
     * @param body is a body of the loop
     */
    public ForLoopNode(IdentifierNode variable, RangeNode range, BodyNode body) {
        this.variable = variable;
        this.range = range;
        this.body = body;
        this.startPosition = null;
    }

    /**
     *
     * A constructor for initializing objects of class ForLoopNode
     * @param variable is an identifier that will be changed each iteration of the loop
     * @param range is an interval in which variable will change
     * @param body is a body of the loop
     * @param startPosition is a start position in the source code
     */
    public ForLoopNode(IdentifierNode variable, RangeNode range, BodyNode body, CodePosition startPosition) {
        this.variable = variable;
        this.range = range;
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
        ForLoopNode that = (ForLoopNode) o;
        return Objects.equals(variable, that.variable) &&
                Objects.equals(range, that.range) &&
                Objects.equals(body, that.body);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("for loop: range = ");
        builder.append(range);

        builder.append("{\n\t");
        builder.append(body);
        builder.append("}");

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, range, body);
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
        return variable != null && variable.validate() &&
                range != null && range.validate() &&
                body != null && body.validate() &&
                startPosition != null;
    }
}
