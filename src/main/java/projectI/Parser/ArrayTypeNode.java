package projectI.Parser;

import java.util.Objects;

public class ArrayTypeNode extends UserTypeNode {
    public final ExpressionNode Size;
    public final TypeNode ElementType;

    public ArrayTypeNode(ExpressionNode size, TypeNode elementType) {
        Size = size;
        ElementType = elementType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayTypeNode that = (ArrayTypeNode) o;
        return Objects.equals(Size, that.Size) &&
                Objects.equals(ElementType, that.ElementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Size, ElementType);
    }
}
