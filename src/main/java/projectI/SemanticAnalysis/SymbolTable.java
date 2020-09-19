package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimeType;

import java.util.HashMap;

public class SymbolTable {
    public final HashMap<ASTNode, HashMap<String, RuntimeType>> types = new HashMap<>();

    public void defineType(ASTNode node, String identifier, RuntimeType runtimeType) throws SemanticAnalysisException {
        if (runtimeType instanceof InvalidRuntimeType)
            throw new UndefinedSymbolException(this, node, identifier);

        if (!types.containsKey(node)) {
            var map = new HashMap<String, RuntimeType>();
            types.put(node, map);
        }

        var map = types.get(node);
        var scope = node;

        while (scope != null) {
            if (types.containsKey(scope)) {
                var scopeMap = types.get(node);
                if (scopeMap.containsKey(identifier))
                    throw new IdentifierAlreadyDefinedException(this, scope, identifier);
            }

            scope = scope.getParent();
        }

        map.put(identifier, runtimeType);
    }

    public RuntimeType tryGetType(ASTNode scope, String identifier) {
        while (scope != null) {
            if (types.containsKey(scope)) {
                var types = this.types.get(scope);
                if (types.containsKey(identifier))
                    return types.get(identifier);
            }

            scope = scope.getParent();
        }

        return new InvalidRuntimeType();
    }
}
