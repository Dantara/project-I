package projectI.AST.Primary;

import projectI.CodePosition;

import java.util.Objects;

public class BooleanLiteralNode implements PrimaryNode {
    public final boolean value;
    public final CodePosition position;

    private BooleanLiteralNode(boolean value, CodePosition position) {
        this.value = value;
        this.position = position;
    }

    public static BooleanLiteralNode create(boolean value) {
        return value ? trueLiteral : falseLiteral;
    }

    public static BooleanLiteralNode create(boolean value, CodePosition position) {
        return new BooleanLiteralNode(value, position);
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

    public static final BooleanLiteralNode trueLiteral = new BooleanLiteralNode(true, null);
    public static final BooleanLiteralNode falseLiteral = new BooleanLiteralNode(false, null);

    @Override
    public CodePosition getPosition() {
        return position;
    }
}
