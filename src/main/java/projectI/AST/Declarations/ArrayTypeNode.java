package projectI.AST.Declarations;

import projectI.AST.Expressions.ExpressionNode;

import java.util.Objects;

public class ArrayTypeNode extends UserTypeNode {
    public final ExpressionNode size;
    public final TypeNode elementType;

    public ArrayTypeNode(ExpressionNode size, TypeNode elementType) {
        this.size = size;
        this.elementType = elementType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayTypeNode that = (ArrayTypeNode) o;
        return Objects.equals(size, that.size) &&
                Objects.equals(elementType, that.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, elementType);
    }

    @Override
    public String toString() {
        if (size == null)
            return "array [] " + elementType;

        return "array [" + size + "] " + elementType;
    }
}
