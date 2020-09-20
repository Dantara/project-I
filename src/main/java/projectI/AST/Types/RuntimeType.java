package projectI.AST.Types;

public interface RuntimeType{
    boolean canBeCastedTo(RuntimeType otherType);
}
