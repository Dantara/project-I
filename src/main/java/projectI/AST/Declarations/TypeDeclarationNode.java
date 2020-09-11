package projectI.AST.Declarations;

import projectI.CodePosition;

import java.util.Objects;

public class TypeDeclarationNode extends SimpleDeclarationNode {
    public final IdentifierNode identifier;
    public final TypeNode type;
    public final CodePosition startPosition;

    /**
     * A constructor for initializing objects of class TypeDeclarationNode
     * @param identifier is an identifier for declaration
     * @param type is type to declare
     */
    public TypeDeclarationNode(IdentifierNode identifier, TypeNode type) {
        this.identifier = identifier;
        this.type = type;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class TypeDeclarationNode
     * @param identifier is an identifier for declaration
     * @param type is type to declare
     * @param startPosition is a start position in the source code
     */
    public TypeDeclarationNode(IdentifierNode identifier, TypeNode type, CodePosition startPosition) {
        this.identifier = identifier;
        this.type = type;
        this.startPosition = startPosition;
    }

    /**
     * Check whether this object is equal to the passed one.
     * @param o the object to check the equality with
     * @return true if this object is equal to the passed one, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDeclarationNode that = (TypeDeclarationNode) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(type, that.type);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier, type);
    }

    /**
     * Find start position in the source code
     * @return start position
     */
    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return identifier != null && identifier.validate() &&
                type != null && type.validate() &&
                startPosition != null;
    }
}
