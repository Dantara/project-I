package projectI.AST.Declarations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecordTypeNode extends UserTypeNode {
    public final List<VariableDeclarationNode> variables = new ArrayList<>();

    public RecordTypeNode addVariable(VariableDeclarationNode variable) {
        variables.add(variable);
        return this;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }

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
}
