package projectI.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramNode implements ASTNode {
    public final List<DeclarationNode> Declarations = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramNode that = (ProgramNode) o;
        if (Declarations.size() != that.Declarations.size()) return false;

        for (int index = 0; index < Declarations.size(); index++) {
            if (!Declarations.get(index).equals(that.Declarations.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Declarations);
    }
}
