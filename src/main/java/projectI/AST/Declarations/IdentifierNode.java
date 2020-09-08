package projectI.AST.Declarations;

import projectI.CodePosition;

import java.util.Objects;

public class IdentifierNode implements TypeNode {
    public final String name;
    public final CodePosition position;

    public IdentifierNode(String name) {
        this.name = name;
        this.position = null;
    }

    public IdentifierNode(String name, CodePosition position) {
        this.name = name;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierNode that = (IdentifierNode) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean validate() {
        return name != null && position != null;
    }
}
