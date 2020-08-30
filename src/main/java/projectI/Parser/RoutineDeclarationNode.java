package projectI.Parser;

import java.util.Objects;

public class RoutineDeclarationNode extends DeclarationNode {
    public final IdentifierNode name;
    public final ParametersNode parameters;
    public final TypeNode returnType;
    public final BodyNode body;

    public RoutineDeclarationNode(IdentifierNode name, ParametersNode parameters, TypeNode returnType, BodyNode body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public RoutineDeclarationNode(IdentifierNode name, ParametersNode parameters, BodyNode body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutineDeclarationNode that = (RoutineDeclarationNode) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters, returnType, body);
    }
}
