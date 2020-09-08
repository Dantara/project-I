package projectI.AST.Primary;

import projectI.CodePosition;

import java.util.Objects;

public class RealLiteralNode implements PrimaryNode {
    public final double value;
    public final Sign sign;
    public final CodePosition valuePosition;

    public RealLiteralNode(double value, CodePosition valuePosition) {
        this.value = value;
        this.valuePosition = valuePosition;
        this.sign = null;
    }

    public RealLiteralNode(double value, Sign sign, CodePosition valuePosition) {
        this.value = value;
        this.sign = sign;
        this.valuePosition = valuePosition;
    }

    public RealLiteralNode(double value) {
        this.value = value;
        this.valuePosition = null;
        this.sign = null;
    }

    public RealLiteralNode(double value, Sign sign) {
        this.value = value;
        this.sign = sign;
        this.valuePosition = null;
    }

    public static RealLiteralNode plus(double value) {
        return new RealLiteralNode(value, Sign.PLUS);
    }

    public static RealLiteralNode minus(double value) {
        return new RealLiteralNode(value, Sign.MINUS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealLiteralNode that = (RealLiteralNode) o;
        return Double.compare(that.value, value) == 0 && sign == that.sign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, sign);
    }

    @Override
    public CodePosition getPosition() {
        return valuePosition;
    }

    public enum Sign {
        PLUS, MINUS
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        if (sign != null)
            builder.append(sign.toString());

        builder.append(value);
        return builder.toString();
    }
}
