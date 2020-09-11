package projectI.AST.Flow;

import projectI.AST.Declarations.BodyNode;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Statements.StatementNode;
import projectI.CodePosition;

import java.util.Objects;

public class ForLoopNode implements StatementNode {
    public final IdentifierNode variable;
    public final RangeNode range;
    public final BodyNode body;
    public final CodePosition startPosition;

    public ForLoopNode(IdentifierNode variable, RangeNode range, BodyNode body) {
        this.variable = variable;
        this.range = range;
        this.body = body;
        this.startPosition = null;
    }

    public ForLoopNode(IdentifierNode variable, RangeNode range, BodyNode body, CodePosition startPosition) {
        this.variable = variable;
        this.range = range;
        this.body = body;
        this.startPosition = startPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForLoopNode that = (ForLoopNode) o;
        return Objects.equals(variable, that.variable) &&
                Objects.equals(range, that.range) &&
                Objects.equals(body, that.body);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("for loop: range = ");
        builder.append(range);

        builder.append("{\n\t");
        builder.append(body);
        builder.append("}");

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, range, body);
    }

    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    @Override
    public boolean validate() {
        return variable != null && variable.validate() &&
                range != null && range.validate() &&
                body != null && body.validate() &&
                startPosition != null;
    }
}
