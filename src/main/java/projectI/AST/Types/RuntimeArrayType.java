package projectI.AST.Types;

import java.util.Objects;

public final class RuntimeArrayType implements RuntimeType {
    public RuntimeArrayType(RuntimeType elementType, int size) {
        ElementType = elementType;
        Size = size;
    }

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return equals(otherType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeArrayType that = (RuntimeArrayType) o;
        return Size == that.Size &&
                Objects.equals(ElementType, that.ElementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ElementType, Size);
    }

    public final RuntimeType ElementType;
    public final int Size;
}
