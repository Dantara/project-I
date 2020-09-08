package projectI.AST.Declarations;

import projectI.AST.Expressions.ExpressionNode;
import projectI.CodePosition;

import java.util.Objects;

public class VariableDeclarationNode extends SimpleDeclarationNode {
    public final IdentifierNode identifier;
    public final TypeNode type;
    public final ExpressionNode expression;
    public final CodePosition startPosition;

    public VariableDeclarationNode(IdentifierNode identifier, TypeNode type, ExpressionNode expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
        this.startPosition = null;
    }

    public VariableDeclarationNode(IdentifierNode identifier, TypeNode type, ExpressionNode expression, CodePosition startPosition) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
        this.startPosition = startPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableDeclarationNode that = (VariableDeclarationNode) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(type, that.type) &&
                Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type, expression);
    }

    @Override
    public String toString() {
        return "var " + identifier + ": " + type + " is " + expression;
    }

    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    @Override
    public boolean validate() {
        return identifier != null && identifier.validate() &&
                (type != null && type.validate() || expression != null && expression.validate()) &&
                startPosition != null;
    }
}
