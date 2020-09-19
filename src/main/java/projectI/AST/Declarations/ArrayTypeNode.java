package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Types.RuntimeArrayType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.InvalidRuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.Objects;

/**
 * Node of user-defined type array
 */
public class ArrayTypeNode extends UserTypeNode {
    public final ExpressionNode size;
    public final TypeNode elementType;
    public final CodePosition startPosition;
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
     * A constructor for initializing objects of class ArrayTypeNode
     * @param size is a number of elements inside the array
     * @param elementType is a type of array's elements
     */
    public ArrayTypeNode(ExpressionNode size, TypeNode elementType) {
        this.size = size;
        this.elementType = elementType;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class ArrayTypeNode
     * @param size is a number of elements inside the array
     * @param elementType is a type of array's elements
     * @param startPosition is a start position in the source code
     */
    public ArrayTypeNode(ExpressionNode size, TypeNode elementType, CodePosition startPosition) {
        this.size = size;
        this.elementType = elementType;
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
        ArrayTypeNode that = (ArrayTypeNode) o;
        return Objects.equals(size, that.size) &&
                Objects.equals(elementType, that.elementType);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(size, elementType);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        if (size == null)
            return "array [] " + elementType;

        return "array [" + size + "] " + elementType;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return elementType != null && elementType.validate() &&
                (size == null || size.validate()) &&
                startPosition != null;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        if (size == null) {
            return new RuntimeArrayType(elementType.getType(symbolTable), null);
        }

        var size = this.size.tryEvaluateConstant();
        if (size == null) return new InvalidRuntimeType();
        if (size instanceof Boolean) {
            size = ((Boolean) size) ? 1 : 0;
        }
        if (size instanceof Double) {
            size = (int) ((double) size);
        }

        return new RuntimeArrayType(elementType.getType(symbolTable), (Integer) size);
    }
}
