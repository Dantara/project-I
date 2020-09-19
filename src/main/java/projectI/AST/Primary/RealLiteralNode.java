package projectI.AST.Primary;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.Objects;

public class RealLiteralNode implements PrimaryNode {
    public final double value;
    public final Sign sign;
    public final CodePosition valuePosition;
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
     * A constructor for initializing objects of class RealLiteralNode
     * @param value is a real value
     * @param valuePosition is a position of the value in the source code
     */
    public RealLiteralNode(double value, CodePosition valuePosition) {
        this.value = value;
        this.valuePosition = valuePosition;
        this.sign = null;
    }

    /**
     * A constructor for initializing objects of class RealLiteralNode
     * @param value is a real value
     * @param sign is a sign of the value
     * @param valuePosition is a position of the value in the source code
     */
    public RealLiteralNode(double value, Sign sign, CodePosition valuePosition) {
        this.value = value;
        this.sign = sign;
        this.valuePosition = valuePosition;
    }

    /**
     * A constructor for initializing objects of class RealLiteralNode
     * @param value is a real value
     */
    public RealLiteralNode(double value) {
        this.value = value;
        this.valuePosition = null;
        this.sign = null;
    }

    /**
     * A constructor for initializing objects of class RealLiteralNode
     * @param value is a real value
     * @param sign is a sign of the value
     */
    public RealLiteralNode(double value, Sign sign) {
        this.value = value;
        this.sign = sign;
        this.valuePosition = null;
    }

    /**
     * Add positive sign to the value of Real Literal
     * @param value is a real value
     * @return Real Literal with positive sign
     */
    public static RealLiteralNode plus(double value) {
        return new RealLiteralNode(value, Sign.PLUS);
    }

    /**
     * Add negative sign to the value of Real Literal
     * @param value is a real value
     * @return Real Literal with negative sign
     */
    public static RealLiteralNode minus(double value) {
        return new RealLiteralNode(value, Sign.MINUS);
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
        RealLiteralNode that = (RealLiteralNode) o;
        return Double.compare(that.value, value) == 0 && sign == that.sign;
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, sign);
    }

    /**
     * Find a position in the source code
     * @return the position
     */
    @Override
    public CodePosition getPosition() {
        return valuePosition;
    }

    @Override
    public Object tryEvaluateConstant() {
        return value;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        return new RuntimePrimitiveType(PrimitiveType.REAL);
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return valuePosition != null;
    }

    public enum Sign {
        PLUS, MINUS
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();

        if (sign != null)
            builder.append(sign.toString());

        builder.append(value);
        return builder.toString();
    }
}
