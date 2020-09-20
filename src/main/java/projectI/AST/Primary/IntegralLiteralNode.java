package projectI.AST.Primary;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.Objects;

public class IntegralLiteralNode implements PrimaryNode {
    public final int value;
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
     * A constructor for initializing objects of class SimpleNode
     * @param value is an integer value
     * @param valuePosition is a position in the source code
     */
    public IntegralLiteralNode(int value, CodePosition valuePosition) {
        this.value = value;
        this.valuePosition = valuePosition;
        this.sign = null;
    }

    /**
     * A constructor for initializing objects of class SimpleNode
     * @param value is an integer value
     * @param sign is a sign of the value
     * @param valuePosition is a position in the source code
     */
    public IntegralLiteralNode(int value, Sign sign, CodePosition valuePosition) {
        this.value = value;
        this.sign = sign;
        this.valuePosition = valuePosition;
    }

    /**
     * A constructor for initializing objects of class SimpleNode
     * @param value is an integer value
     */
    public IntegralLiteralNode(int value) {
        this.value = value;
        this.sign = null;
        this.valuePosition = null;
    }

    /**
     * A constructor for initializing objects of class SimpleNode
     * @param value is an integer value
     * @param sign is a sign of the value
     */
    public IntegralLiteralNode(int value, Sign sign) {
        this.value = value;
        this.sign = sign;
        this.valuePosition = null;
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
        IntegralLiteralNode that = (IntegralLiteralNode) o;
        return value == that.value && sign == that.sign;
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
    public Object tryEvaluateConstant(SymbolTable symbolTable)
    {
        if (sign == null) return value;

        switch (sign) {
            case MINUS: return -value;
            case PLUS: return value;
            case NOT:
                 if (value == 0) return true;
                 if (value == 1) return false;
                 return null;
        };

        return null;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        return new RuntimePrimitiveType(PrimitiveType.INTEGER);
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
        PLUS, MINUS, NOT
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

    /**
     * Add positive sign to the value
     * @param value is an integer value of the Integral Literal
     * @return Literal Integral with the sign
     */
    public static IntegralLiteralNode plus(int value) {
        return new IntegralLiteralNode(value, Sign.PLUS);
    }

    /**
     * Add negative sign to the value
     * @param value is an integer value of the Integral Literal
     * @return Literal Integral with the sign
     */
    public static IntegralLiteralNode minus(int value) {
        return new IntegralLiteralNode(value, Sign.MINUS);
    }

    /**
     * Add no sign to the value
     * @param value is an integer value of the Integral Literal
     * @return Literal Integral with no sign
     */
    public static IntegralLiteralNode not(int value) {
        return new IntegralLiteralNode(value, Sign.NOT);
    }
}
