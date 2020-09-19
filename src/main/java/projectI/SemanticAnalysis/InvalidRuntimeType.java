package projectI.SemanticAnalysis;

import projectI.AST.Types.RuntimeType;

public final class InvalidRuntimeType implements RuntimeType {
    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return equals(otherType);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass();
    }
}
