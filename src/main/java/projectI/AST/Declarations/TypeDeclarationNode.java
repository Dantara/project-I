package projectI.AST.Declarations;

import projectI.CodePosition;

import java.util.Objects;

public class TypeDeclarationNode extends SimpleDeclarationNode {
    public final IdentifierNode identifier;
    public final TypeNode type;
    public final CodePosition startPosition;

    public TypeDeclarationNode(IdentifierNode identifier, TypeNode type) {
        this.identifier = identifier;
        this.type = type;
        this.startPosition = null;
    }

    public TypeDeclarationNode(IdentifierNode identifier, TypeNode type, CodePosition startPosition) {
        this.identifier = identifier;
        this.type = type;
        this.startPosition = startPosition;
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

    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    @Override
    public boolean validate() {
        return identifier != null && identifier.validate() &&
                type != null && type.validate() &&
                startPosition != null;
    }
}
