package projectI.AST.Types;

import java.util.Objects;

public final class RuntimeArrayType implements RuntimeType {
    public RuntimeArrayType(RuntimeType elementType, Integer size) {
        this.elementType = elementType;
        this.size = size;
    }

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        if (equals(otherType)) return true;
        if (!(otherType instanceof RuntimeArrayType)) return false;
        var otherArray = (RuntimeArrayType) otherType;

        return elementType.equals(otherArray.elementType) &&
                (otherArray.size == null || Objects.equals(size, otherArray.size));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeArrayType that = (RuntimeArrayType) o;
        return Objects.equals(size, that.size) &&
                Objects.equals(elementType, that.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, size);
    }

    public final RuntimeType elementType;
    public final Integer size;

    @Override
    public String toString() {
        return String.format("array[%s] %s", size, elementType);
    }
}
