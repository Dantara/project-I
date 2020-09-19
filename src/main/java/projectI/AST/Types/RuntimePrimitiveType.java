package projectI.AST.Types;

import java.util.Objects;
import projectI.AST.Declarations.PrimitiveType;

public final class RuntimePrimitiveType implements RuntimeType {
    public RuntimePrimitiveType(projectI.AST.Declarations.PrimitiveType type) {
        this.type = type;
    }

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        if (otherType instanceof RuntimePrimitiveType) {
            var otherPrimitiveType = (RuntimePrimitiveType) otherType;
            if (type == otherPrimitiveType.type) return true;

            return switch (otherPrimitiveType.type) {
                case INTEGER, REAL -> true;
                case BOOLEAN -> type == PrimitiveType.INTEGER || type == PrimitiveType.BOOLEAN;
            };
        }

        return false;
    }

    public final PrimitiveType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimePrimitiveType that = (RuntimePrimitiveType) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
