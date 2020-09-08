package projectI.AST.Statements;

import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Primary.PrimaryNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutineCallNode implements StatementNode, PrimaryNode {
    public final IdentifierNode name;
    public final List<ExpressionNode> arguments = new ArrayList<>();
    public final CodePosition startPosition;

    public RoutineCallNode addArgument(ExpressionNode expression) {
        arguments.add(expression);
        return this;
    }

    public RoutineCallNode(IdentifierNode name, CodePosition startPosition) {
        this.name = name;
        this.startPosition = startPosition;
    }

    public RoutineCallNode(IdentifierNode name) {
        this.name = name;
        this.startPosition = null;
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

    @Override
    public CodePosition getPosition() {
        return startPosition;
    }
}
