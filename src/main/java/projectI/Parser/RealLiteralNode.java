package projectI.Parser;

import java.util.Objects;

public class RealLiteralNode implements PrimaryNode {
    public final double value;
    public final Sign sign;

    public RealLiteralNode(double value) {
        this.value = value;
        sign = null;
    }

    public RealLiteralNode(double value, Sign sign) {
        this.value = value;
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealLiteralNode that = (RealLiteralNode) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
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
