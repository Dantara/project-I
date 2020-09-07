package projectI.AST.Statements;

import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.CodePosition;

import java.util.Objects;

public class AssignmentNode implements StatementNode {
    public final ModifiablePrimaryNode modifiable;
    public final ExpressionNode assignedValue;

    public AssignmentNode(ModifiablePrimaryNode modifiable, ExpressionNode assignedValue) {
        this.modifiable = modifiable;
        this.assignedValue = assignedValue;
    }

    public CodePosition getStartPosition() {
        return modifiable.startPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentNode that = (AssignmentNode) o;
        return Objects.equals(modifiable, that.modifiable) &&
                Objects.equals(assignedValue, that.assignedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiable, assignedValue);
    }

    @Override
    public String toString() {
        return modifiable + " := " + assignedValue;
    }
}
