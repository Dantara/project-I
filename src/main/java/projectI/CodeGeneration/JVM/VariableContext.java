package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import projectI.AST.ASTNode;
import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.HashMap;

public class VariableContext extends HashMap<Pair<ASTNode, String>, Integer> {
    public final RoutineDeclarationNode routine;

    public VariableContext(SymbolTable symbolTable, RoutineDeclarationNode routine) {
        this.routine = routine;

        for (var parameter : routine.parameters.parameters) {
            defineVariable(routine, parameter.getValue0().name, parameter.getValue1().getType(symbolTable));
        }
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

    public int defineVariable(ASTNode context, String identifier, RuntimeType variableType) {
        var id = localVariablesCount;
        this.put(new Pair<>(context, identifier), id);
        localVariablesCount += getTypeOffset(variableType);
        return id;
    }

    private int getTypeOffset(RuntimeType type) {
        if (type instanceof RuntimePrimitiveType) {
            return switch (((RuntimePrimitiveType) type).type) {
                case REAL -> 2;
                case INTEGER, BOOLEAN -> 1;
            };
        }

        return 1;
    }

    private int localVariablesCount = 0;
}
