package projectI.AST;

import projectI.AST.Declarations.DeclarationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramNode implements ASTNode {
    public final List<DeclarationNode> declarations = new ArrayList<>();

    /**
     * Add declaration to the program node
     * @param declaration is a declaration node to add
     * @return Program Node with added declaration node
     */
    public ProgramNode addDeclaration(DeclarationNode declaration) {
        declarations.add(declaration);
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
        ProgramNode that = (ProgramNode) o;
        if (declarations.size() != that.declarations.size()) return false;

        for (int index = 0; index < declarations.size(); index++) {
            if (!declarations.get(index).equals(that.declarations.get(index)))
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
        return Objects.hash(declarations);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("program={\n");

        for (var declaration : declarations) {
            builder.append("\t");
            builder.append(declaration);
            builder.append("\n");
        }

        builder.append("}");
        return builder.toString();
    }

    @Override
    public ASTNode getParent() {
        return null;
    }

    @Override
    public void setParent(ASTNode parent) {
        throw new IllegalStateException();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        for (var declaration : declarations) {
            if (declaration == null || !declaration.validate())
                return false;
        }

        return true;
    }
}
