package projectI.AST.Types;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Objects;

public final class RecordType implements RuntimeType {
    public final ArrayList<Pair<RuntimeType, Object>> variables = new ArrayList<>();

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return equals(otherType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordType that = (RecordType) o;
        if (variables.size() != that.variables.size()) return false;

        for (int index = 0; index < variables.size(); index++) {
            var variable = variables.get(index);
            var thatVariable = that.variables.get(index);
            if (!variable.getValue0().equals(thatVariable.getValue0())) return false;
            if (!variable.getValue1().equals(thatVariable.getValue1())) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}
