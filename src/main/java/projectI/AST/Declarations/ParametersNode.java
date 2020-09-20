package projectI.AST.Declarations;

import org.javatuples.Pair;
import projectI.AST.ASTNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParametersNode implements ASTNode {
    public ASTNode parent;

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    /**
     * Start position in the source code
     */
    public final CodePosition startPosition;

    /**
     * List of parameters
     */
    public final List<Pair<IdentifierNode, TypeNode>> parameters = new ArrayList<>();

    /**
     * A constructor for initializing objects of class ParametersNode
     */
    public ParametersNode() {
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class ParametersNode
     * @param startPosition is start position in the source code
     */
    public ParametersNode(CodePosition startPosition) {
        this.startPosition = startPosition;
    }


    /**
     * Add a parameter to the list of parameters
     * @param identifier is identifier of the parameter to add
     * @param type is type of the parameter to add
     * @return ParametersNode itself
     */
    public ParametersNode addParameter(IdentifierNode identifier, TypeNode type) {
        parameters.add(new Pair<>(identifier, type));
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
        ParametersNode that = (ParametersNode) o;
        if (parameters.size() != that.parameters.size()) return false;

        for (int index = 0; index < parameters.size(); index++) {
            if (!parameters.get(index).equals(that.parameters.get(index)))
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
        return Objects.hash(parameters);
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        if (parameters.size() > 0 && startPosition == null) return false;

        for (var parameter : parameters) {
            if (parameter == null)
                return false;

            if (parameter.getValue0() == null || !parameter.getValue0().validate())
                return false;

            if (parameter.getValue1() == null || !parameter.getValue1().validate())
                return false;
        }

        return true;
    }
}
