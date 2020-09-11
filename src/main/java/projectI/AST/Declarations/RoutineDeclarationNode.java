package projectI.AST.Declarations;

import projectI.CodePosition;

import java.util.Objects;

/**
 * A node for Routine Declaration Representation
 */
public class RoutineDeclarationNode extends DeclarationNode {
    public final IdentifierNode name;
    public final ParametersNode parameters;
    public final TypeNode returnType;
    public final BodyNode body;
    public final CodePosition startPosition;

    /**
     * A constructor for initializing objects of class RoutineDeclarationNode
     * @param name is a name of Routine
     * @param parameters is a list of routine's parameters
     * @param returnType is a type that will be returned by routine
     * @param body is a routine body
     */
    public RoutineDeclarationNode(IdentifierNode name, ParametersNode parameters, TypeNode returnType, BodyNode body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class RoutineDeclarationNode
     * @param name is a name of Routine
     * @param parameters is a list of routine's parameters
     * @param body is a routine body
     */
    public RoutineDeclarationNode(IdentifierNode name, ParametersNode parameters, BodyNode body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = null;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class RoutineDeclarationNode
     * @param name is a name of Routine
     * @param parameters is a list of routine's parameters
     * @param returnType is a type that will be returned by routine
     * @param body is a routine body
     * @param startPosition is a start position in the source code
     */
    public RoutineDeclarationNode(IdentifierNode name, ParametersNode parameters, TypeNode returnType, BodyNode body, CodePosition startPosition) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
        this.startPosition = startPosition;
    }

    /**
     * A constructor for initializing objects of class RoutineDeclarationNode
     * @param name is a name of Routine
     * @param parameters is a list of routine's parameters
     * @param body is a routine body
     * @param startPosition is a start position in the source code
     */
    public RoutineDeclarationNode(IdentifierNode name, ParametersNode parameters, BodyNode body, CodePosition startPosition) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = null;
        this.startPosition = startPosition;
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
        RoutineDeclarationNode that = (RoutineDeclarationNode) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(body, that.body);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, parameters, returnType, body);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("routine name='");
        builder.append(name);
        builder.append("' parameters = '");

        for (var parameter : parameters.parameters) {
            builder.append(parameter);
            builder.append(',');
        }

        builder.append("'");

        builder.append(": ");
        builder.append(returnType);

        builder.append("{\n");

        for (var statement : body.statements) {
            builder.append("\t");
            builder.append(statement);
            builder.append("\n");
        }

        builder.append("}");

        return builder.toString();
    }

    /**
     * Find start position in the source code
     * @return start position
     */
    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        return name != null && name.validate() &&
                parameters != null && parameters.validate() &&
                (returnType == null || returnType.validate()) &&
                body != null && body.validate() &&
                startPosition != null;
    }
}
