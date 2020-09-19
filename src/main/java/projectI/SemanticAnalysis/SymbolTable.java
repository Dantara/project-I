package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimeType;

import java.util.HashMap;

public class SymbolTable {
    public final HashMap<ASTNode, HashMap<String, RuntimeType>> types = new HashMap<>();

    public void clear() {
        types.clear();
    }

    public void defineType(ASTNode node, String identifier, RuntimeType runtimeType) {
        if (!types.containsKey(node)) {
            var map = new HashMap<String, RuntimeType>();
            types.put(node, map);
        }

        var map = types.get(node);
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
