package projectI.AST.Primary;

import java.util.Objects;

public class BooleanLiteralNode implements PrimaryNode {
    public final boolean value;

    private BooleanLiteralNode(boolean value) {
        this.value = value;
    }

    public static BooleanLiteralNode create(boolean value) {
        return value ? trueLiteral : falseLiteral;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanLiteralNode that = (BooleanLiteralNode) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static final BooleanLiteralNode trueLiteral = new BooleanLiteralNode(true);
    public static final BooleanLiteralNode falseLiteral = new BooleanLiteralNode(false);
}
