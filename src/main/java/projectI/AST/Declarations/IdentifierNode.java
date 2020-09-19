package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.InvalidRuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.Objects;

/**
 * Node of identifier
 */
public class IdentifierNode implements TypeNode {
    public final String name;
    public final CodePosition position;
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
     * A constructor for initializing objects of class IdentifierNode
     * @param name is a name of identifier
     */
    public IdentifierNode(String name) {
        this.name = name;
        this.position = null;
    }

    /**
     * A constructor for initializing objects of class IdentifierNode
     * @param name is a name of identifier
     * @param position is a start position in the source code
     */
    public IdentifierNode(String name, CodePosition position) {
        this.name = name;
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
        IdentifierNode that = (IdentifierNode) o;
        return Objects.equals(name, that.name);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return name != null && position != null;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        var definedType = symbolTable.tryGetType(this, name);
        return definedType != null ? definedType : new InvalidRuntimeType();
    }
}
