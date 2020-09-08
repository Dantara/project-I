package projectI.AST.Declarations;

import java.util.Objects;

public class PrimitiveTypeNode implements TypeNode {
    public PrimitiveTypeNode(PrimitiveType type) {
        this.type = type;
    }

    public final PrimitiveType type;

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

    @Override
    public String toString() {
        return type.toString();
    }
}
