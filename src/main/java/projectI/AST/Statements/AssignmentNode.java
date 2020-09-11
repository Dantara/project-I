package projectI.AST.Statements;

import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.CodePosition;

import java.util.Objects;

public class AssignmentNode implements StatementNode {
    public final ModifiablePrimaryNode modifiable;
    public final ExpressionNode assignedValue;

    /**
     * A constructor for initializing objects of class AssignmentNode
     * @param modifiable is a modifiable primary
     * @param assignedValue is an expression assigned to the modifiable primary
     */
    public AssignmentNode(ModifiablePrimaryNode modifiable, ExpressionNode assignedValue) {
        this.modifiable = modifiable;
        this.assignedValue = assignedValue;
    }

    /**
     * Find a start position in the source code
     * @return the position
     */
    public CodePosition getStartPosition() {
        return modifiable.startPosition;
    }

    /**
     * Check whether this object is equal to the passed one.
     * @param o the object to check the equality with
     * @return true if this object is equal to the passed one, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentNode that = (AssignmentNode) o;
        return Objects.equals(modifiable, that.modifiable) &&
                Objects.equals(assignedValue, that.assignedValue);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(modifiable, assignedValue);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        return modifiable + " := " + assignedValue;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return modifiable != null && modifiable.validate() &&
                assignedValue != null && assignedValue.validate();
    }
}
