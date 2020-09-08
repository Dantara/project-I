package projectI.AST.Declarations;

import org.javatuples.Pair;
import projectI.AST.ASTNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParametersNode implements ASTNode {
    public final CodePosition startPosition;
    public final List<Pair<IdentifierNode, TypeNode>> parameters = new ArrayList<>();

    public ParametersNode() {
        this.startPosition = null;
    }

    public ParametersNode(CodePosition startPosition) {
        this.startPosition = startPosition;
    }


    public ParametersNode addParameter(IdentifierNode identifier, TypeNode type) {
        parameters.add(new Pair<>(identifier, type));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametersNode that = (ParametersNode) o;
        if (parameters.size() != that.parameters.size()) return false;

        for (int index = 0; index < parameters.size(); index++) {
            if (!parameters.get(index).equals(that.parameters.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}
