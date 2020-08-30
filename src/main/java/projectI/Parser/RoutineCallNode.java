package projectI.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutineCallNode implements StatementNode {
    public final IdentifierNode name;
    public final List<ExpressionNode> arguments = new ArrayList<>();

    public RoutineCallNode(IdentifierNode name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutineCallNode that = (RoutineCallNode) o;
        if (arguments.size() != that.arguments.size()) return false;

        for (int index = 0; index < arguments.size(); index++) {
            if (!arguments.get(index).equals(that.arguments.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }
}
