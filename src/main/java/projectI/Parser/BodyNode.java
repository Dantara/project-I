package projectI.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BodyNode implements ASTNode {
    public final List<StatementNode> statements = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BodyNode bodyNode = (BodyNode) o;
        if (statements.size() != bodyNode.statements.size()) return false;

        for (int index = 0; index < statements.size(); index++) {
            if (!statements.get(index).equals(bodyNode.statements.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }
}
