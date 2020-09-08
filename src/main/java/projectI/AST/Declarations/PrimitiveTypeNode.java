package projectI.AST.Declarations;

import projectI.CodePosition;

import java.util.Objects;

public class PrimitiveTypeNode implements TypeNode {
    public final CodePosition position;

    public PrimitiveTypeNode(PrimitiveType type) {
        this.type = type;
        this.position = null;
    }

    public PrimitiveTypeNode(PrimitiveType type, CodePosition position) {
        this.type = type;
        this.position = position;
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
