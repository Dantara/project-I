package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import projectI.AST.ASTNode;
import projectI.AST.Declarations.RoutineDeclarationNode;

import java.util.HashMap;

public class VariableContext extends HashMap<Pair<ASTNode, String>, Integer> {
    public Integer tryGetIndexOf(ASTNode context, String identifier) {
        var node = context;

        while (node != null) {
            var key = new Pair<>(node, identifier);
            if (this.containsKey(key))
                return this.get(key);

            node = node.getParent();
        }

        return null;
    }

    public int defineVariable(RoutineDeclarationNode routine, ASTNode context, String identifier) {
        var count = (int) localVariablesCount.getOrDefault(routine, 0);
        var id = count;
        this.put(new Pair<>(context, identifier), id);

        count++;
        localVariablesCount.put(routine, count);
        return id;
    }

    private final HashMap<RoutineDeclarationNode, Integer> localVariablesCount = new HashMap<>();
}
