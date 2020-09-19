package projectI.AST.Primary;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.Objects;

public class BooleanLiteralNode implements PrimaryNode {
    public final boolean value;
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
     * A constructor for initializing objects of class BooleanLiteralNode
     * @param value is true/false
     * @param position is a position in the source code
     */
    private BooleanLiteralNode(boolean value, CodePosition position) {
        this.value = value;
        this.position = position;
    }

    /**
     * Create Boolean Literal
     * @param value of the literal
     * @return Boolean Literal Node
     */
    public static BooleanLiteralNode create(boolean value) {
        return value ? trueLiteral : falseLiteral;
    }

    /**
     * Create Boolean Literal
     * @param value of the literal
     * @param position is a position in the source code
     * @return Boolean Literal Node
     */
    public static BooleanLiteralNode create(boolean value, CodePosition position) {
        return new BooleanLiteralNode(value, position);
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
        BooleanLiteralNode that = (BooleanLiteralNode) o;
        return value == that.value;
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static final BooleanLiteralNode trueLiteral = new BooleanLiteralNode(true, null);
    public static final BooleanLiteralNode falseLiteral = new BooleanLiteralNode(false, null);

    /**
     * Find a position in the source code
     * @return the position
     */
    @Override
    public CodePosition getPosition() {
        return position;
    }

    @Override
    public Object tryEvaluateConstant() {
        return value;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        return new RuntimePrimitiveType(PrimitiveType.BOOLEAN);
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return position != null;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
