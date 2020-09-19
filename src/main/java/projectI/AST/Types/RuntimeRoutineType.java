package projectI.AST.Types;

import java.util.ArrayList;
import java.util.Objects;

public class RuntimeRoutineType implements RuntimeType{
    public final ArrayList<RuntimeType> parameters = new ArrayList<>();
    public final RuntimeType returnType;

    public RuntimeRoutineType(RuntimeType returnType) {
        this.returnType = returnType;
    }

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeRoutineType that = (RuntimeRoutineType) o;
        if (parameters.size() != that.parameters.size()) return false;

        for (var index = 0; index < parameters.size(); index++) {
            if (!parameters.get(index).equals(that.parameters.get(index)))
                return false;
        }

        return Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}
