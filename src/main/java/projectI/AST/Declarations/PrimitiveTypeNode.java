package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.Objects;

public class PrimitiveTypeNode implements TypeNode {
    public final CodePosition position;
    public final PrimitiveType type;
    public ASTNode parent;

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    /**
     * A constructor for initializing objects of class PrimitiveTypeNode
     * @param type is a type of a primitive
     */
    public PrimitiveTypeNode(PrimitiveType type) {
        this.type = type;
        this.position = null;
    }

    /**
     * A constructor for initializing objects of class PrimitiveTypeNode
     * @param type is a type of a primitive
     * @param position is a position in the source code
     */
    public PrimitiveTypeNode(PrimitiveType type, CodePosition position) {
        this.type = type;
        this.position = position;
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
        PrimitiveTypeNode that = (PrimitiveTypeNode) o;
        return type == that.type;
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        return type.toString();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return type != null && position != null;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        return new RuntimePrimitiveType(type);
    }
}
