package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.ProgramNode;
import projectI.AST.Types.InvalidRuntimeType;
import projectI.AST.Types.RuntimeType;

import java.util.HashMap;

public class SymbolTable {
    public final HashMap<ASTNode, HashMap<String, RuntimeType>> types = new HashMap<>();

    public void defineType(ASTNode scope, String identifier, RuntimeType runtimeType) throws SemanticAnalysisException {
        if (runtimeType instanceof InvalidRuntimeType)
            throw new UndefinedSymbolException(this, scope, identifier);

        if (!types.containsKey(scope)) {
            var map = new HashMap<String, RuntimeType>();
            types.put(scope, map);
        }

        var map = types.get(scope);

        if (getType(scope, identifier) instanceof InvalidRuntimeType) {
            map.put(identifier, runtimeType);
        } else {
            throw new IdentifierAlreadyDefinedException(this, scope, identifier);
        }
    }

    public RuntimeType getType(ASTNode scope, String identifier) {
        while (scope != null) {
            if (this.types.containsKey(scope)) {
                var definedTypes = this.types.get(scope);
                if (definedTypes.containsKey(identifier))
                    return definedTypes.get(identifier);
            }

            if (!(scope instanceof ProgramNode) && scope.getParent() == null)
                throw new IllegalStateException(String.format("%s is supposed to have a parent but it does not.", scope.toString()));

            scope = scope.getParent();
        }

        return InvalidRuntimeType.instance;
    }
}
