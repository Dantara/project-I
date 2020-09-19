package projectI.AST.Types;

import projectI.AST.Declarations.RecordTypeNode;

import java.util.Objects;

public class RuntimeRecordType implements RuntimeType{
    public final RecordTypeNode record;

    public RuntimeRecordType(RecordTypeNode record) {
        this.record = record;
    }

    @Override
    public boolean canBeCastedTo(RuntimeType otherType) {
        return equals(otherType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeRecordType that = (RuntimeRecordType) o;
        return Objects.equals(record, that.record);
    }

    @Override
    public int hashCode() {
        return Objects.hash(record);
    }
}
