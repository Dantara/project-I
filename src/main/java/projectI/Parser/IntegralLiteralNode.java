package projectI.Parser;

import java.util.Objects;

public class IntegralLiteralNode implements PrimaryNode {
    public final int value;

    public IntegralLiteralNode(int value) {
        this.value = value;
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
}
