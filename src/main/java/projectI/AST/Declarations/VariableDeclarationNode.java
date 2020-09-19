package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.CodePosition;

import java.util.Objects;

public class VariableDeclarationNode extends SimpleDeclarationNode {
    public final IdentifierNode identifier;
    public final TypeNode type;
    public final ExpressionNode expression;
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
     * A constructor for initializing objects of class VariableDeclarationNode
     * @param identifier is an identifier for declaration
     * @param type is a type of a variable
     * @param expression is an expression that will be assigned to declared variable
     */
    public VariableDeclarationNode(IdentifierNode identifier, TypeNode type, ExpressionNode expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class VariableDeclarationNode
     * @param identifier is an identifier for declaration
     * @param type is a type of a variable
     * @param expression is an expression that will be assigned to declared variable
     * @param startPosition is a start position in the source code
     */
    public VariableDeclarationNode(IdentifierNode identifier, TypeNode type, ExpressionNode expression, CodePosition startPosition) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
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
        VariableDeclarationNode that = (VariableDeclarationNode) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(type, that.type) &&
                Objects.equals(expression, that.expression);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier, type, expression);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        return "var " + identifier + ": " + type + " is " + expression;
    }

    /**
     * Find start position in the source code
     * @return start position
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
        return identifier != null && identifier.validate() &&
                (type != null && type.validate() || expression != null && expression.validate()) &&
                startPosition != null;
    }
}
