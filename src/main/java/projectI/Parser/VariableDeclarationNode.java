package projectI.Parser;

import java.util.Objects;

public class VariableDeclarationNode extends SimpleDeclarationNode {
    public final IdentifierNode identifier;
    public final TypeNode type;
    public final ExpressionNode expression;

    public VariableDeclarationNode(IdentifierNode identifier, TypeNode type, ExpressionNode expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
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
}
