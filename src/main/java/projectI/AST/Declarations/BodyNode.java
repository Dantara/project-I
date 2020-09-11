package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.AST.Statements.StatementNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Node of a body inside loops, routine declaration or if statemnt
 */
public class BodyNode implements ASTNode {

    /**
     * List of statements
     */
    public final List<StatementNode> statements = new ArrayList<>();

    /**
     * Add statement to the list of statements
     * @param statement is a statement to add
     * @return Body itself
     */
    public BodyNode add(StatementNode statement) {
        statements.add(statement);
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
        BodyNode bodyNode = (BodyNode) o;
        if (statements.size() != bodyNode.statements.size()) return false;

        for (int index = 0; index < statements.size(); index++) {
            if (!statements.get(index).equals(bodyNode.statements.get(index)))
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
        return Objects.hash(statements);
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        for (var statement : statements) {
            if (statement == null || !statement.validate())
                return false;
        }

        return true;
    }
}
