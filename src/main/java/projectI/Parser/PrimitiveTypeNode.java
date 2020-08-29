package projectI.Parser;

import java.util.Objects;

public class PrimitiveTypeNode implements TypeNode {
    public PrimitiveTypeNode(Type type) {
        this.type = type;
    }

    public enum Type {
        INTEGER, REAL, BOOLEAN
    }

    public final Type type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveTypeNode that = (PrimitiveTypeNode) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
