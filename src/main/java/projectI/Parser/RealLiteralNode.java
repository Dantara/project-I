package projectI.Parser;

import java.util.Objects;

public class RealLiteralNode implements PrimaryNode {
    public final double value;

    public RealLiteralNode(double value) {
        this.value = value;
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
}
