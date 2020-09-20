package projectI.AST.Types;

public final class InvalidRuntimeType implements RuntimeType {
    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return equals(otherType);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass();
    }

    @Override
    public String toString() {
        return "Invalid type";
    }

    private InvalidRuntimeType() {

    }

    public final static InvalidRuntimeType instance = new InvalidRuntimeType();
}
