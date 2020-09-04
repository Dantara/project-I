package projectI.AST.Primary;

import java.util.Objects;

public class IntegralLiteralNode implements PrimaryNode {
    public final int value;
    public final Sign sign;

    public IntegralLiteralNode(int value) {
        this.value = value;
        this.sign = null;
    }

    public IntegralLiteralNode(int value, Sign sign) {
        this.value = value;
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegralLiteralNode that = (IntegralLiteralNode) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public enum Sign {
        PLUS, MINUS, NOT
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        if (sign != null)
            builder.append(sign.toString());

        builder.append(value);
        return builder.toString();
    }

    public static IntegralLiteralNode plus(int value) {
        return new IntegralLiteralNode(value, Sign.PLUS);
    }

    public static IntegralLiteralNode minus(int value) {
        return new IntegralLiteralNode(value, Sign.MINUS);
    }

    public static IntegralLiteralNode not(int value) {
        return new IntegralLiteralNode(value, Sign.NOT);
    }
}
