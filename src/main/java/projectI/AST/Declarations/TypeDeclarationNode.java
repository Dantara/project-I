package projectI.AST.Declarations;

import java.util.Objects;

public class TypeDeclarationNode extends SimpleDeclarationNode {
    public final IdentifierNode identifier;
    public final TypeNode type;

    public TypeDeclarationNode(IdentifierNode identifier, TypeNode type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDeclarationNode that = (TypeDeclarationNode) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type);
    }
}
