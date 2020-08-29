package projectI.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecordTypeNode extends UserTypeNode {
    public final List<VariableDeclarationNode> Variables = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordTypeNode that = (RecordTypeNode) o;
        if (Variables.size() != that.Variables.size()) return false;

        for (int index = 0; index < Variables.size(); index++) {
            if (!Variables.get(index).equals(that.Variables.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Variables);
    }
}
