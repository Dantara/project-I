package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import projectI.AST.ASTNode;
import projectI.AST.Declarations.RoutineDeclarationNode;

import java.util.HashMap;

public class VariableContext extends HashMap<Pair<ASTNode, String>, Integer> {
    public final RoutineDeclarationNode routine;

    public VariableContext(RoutineDeclarationNode routine) {
        this.routine = routine;
        localVariablesCount = routine.parameters.parameters.size();
    }

    public Integer tryGetIndexOf(ASTNode context, String identifier) {
        var node = context;

        while (node != null) {
            var key = new Pair<>(node, identifier);
            if (this.containsKey(key))
                return this.get(key);

            node = node.getParent();
        }

        throw new IllegalStateException();
    }

    public int defineVariable(ASTNode context, String identifier) {
        var id = localVariablesCount;
        this.put(new Pair<>(context, identifier), id);
        localVariablesCount++;
        return id;
    }

    private int localVariablesCount = 0;
}
