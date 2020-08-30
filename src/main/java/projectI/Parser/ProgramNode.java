package projectI.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramNode implements ASTNode {
    public final List<DeclarationNode> declarations = new ArrayList<>();

    public ProgramNode add(DeclarationNode declaration) {
        declarations.add(declaration);
        return this;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(declarations);
    }


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
}
