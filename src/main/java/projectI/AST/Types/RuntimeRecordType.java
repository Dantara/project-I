package projectI.AST.Types;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Objects;

public final class RuntimeRecordType implements RuntimeType {
    public final ArrayList<Triplet<String, RuntimeType, Object>> variables = new ArrayList<>();

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return equals(otherType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeRecordType that = (RuntimeRecordType) o;
        if (variables.size() != that.variables.size()) return false;

        for (int index = 0; index < variables.size(); index++) {
            var variable = variables.get(index);
            var thatVariable = that.variables.get(index);
            if (!Objects.equals(variable.getValue0(), thatVariable.getValue0())) return false;
            if (!Objects.equals(variable.getValue1(), thatVariable.getValue1())) return false;
            if (!Objects.equals(variable.getValue2(), thatVariable.getValue2())) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}
