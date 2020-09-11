package projectI.AST.Declarations;

import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Node of user-defined type record
 */
public class RecordTypeNode extends UserTypeNode {
    public final CodePosition startPosition;

    /**
     * List of record's variables
     */
    public final List<VariableDeclarationNode> variables = new ArrayList<>();

    /**
     * A constructor for initializing objects of class
     * @param startPosition is a start position in the source code
     */
    public RecordTypeNode(CodePosition startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * A constructor for initializing objects of class
     */
    public RecordTypeNode() {
        this.startPosition = null;
    }

    /**
     * Add a variable to the list of variables
     * @param variable is a variablee to add
     * @return RecordTypeNode itself
     */
    public RecordTypeNode addVariable(VariableDeclarationNode variable) {
        variables.add(variable);
        return this;
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
        RecordTypeNode that = (RecordTypeNode) o;
        if (variables.size() != that.variables.size()) return false;

        for (int index = 0; index < variables.size(); index++) {
            if (!variables.get(index).equals(that.variables.get(index)))
                return false;
        }

        return true;
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("record{");

        for (VariableDeclarationNode variable : variables) {
            builder.append(variable);
            builder.append(";");
        }

        builder.append("}");
        return builder.toString();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        if (startPosition == null) return false;

        for (var variable : variables) {
            if (variable == null || !variable.validate())
                return false;
        }

        return true;
    }
}
