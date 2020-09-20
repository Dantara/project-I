package projectI.AST.Types;

import java.util.Objects;

public final class RuntimeArrayType implements RuntimeType {
    public RuntimeArrayType(RuntimeType elementType, Integer size) {
        this.elementType = elementType;
        this.size = size;
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
        return size.equals(that.size) &&
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
