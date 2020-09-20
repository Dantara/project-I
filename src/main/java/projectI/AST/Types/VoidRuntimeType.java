package projectI.AST.Types;

public class VoidRuntimeType implements RuntimeType {
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
        return "VOID";
    }

    private VoidRuntimeType() {

    }

    public final static VoidRuntimeType instance = new VoidRuntimeType();
}
