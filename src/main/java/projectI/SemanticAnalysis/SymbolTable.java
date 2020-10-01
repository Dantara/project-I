package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.ProgramNode;
import projectI.AST.Types.InvalidRuntimeType;
import projectI.AST.Types.RuntimeType;
import projectI.SemanticAnalysis.Exceptions.IdentifierAlreadyDefinedException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;
import projectI.SemanticAnalysis.Exceptions.UndefinedSymbolException;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<ASTNode, HashMap<String, RuntimeType>> types = new HashMap<>();
    private final HashMap<ExpressionNode, Object> constants = new HashMap<>();

    public void defineType(ASTNode scope, String identifier, RuntimeType runtimeType) throws SemanticAnalysisException {
        if (runtimeType instanceof InvalidRuntimeType)
            throw new UndefinedSymbolException(this, scope, identifier);

        if (getType(scope, identifier) instanceof InvalidRuntimeType) {
            var map = getTypesDefinedAt(scope);
            map.put(identifier, runtimeType);
        } else {
            throw new IdentifierAlreadyDefinedException(this, scope, identifier);
        }
    }

    public boolean isDefinedAt(ASTNode scope, String identifier) {
        var types = getTypesDefinedAt(scope);
        return types.containsKey(identifier);
    }

    private HashMap<String, RuntimeType> getTypesDefinedAt(ASTNode scope) {
        if (types.containsKey(scope)) return types.get(scope);

        var scopeTypes = new HashMap<String, RuntimeType>();
        types.put(scope, scopeTypes);
        return scopeTypes;
    }

    public void defineConstant(ExpressionNode expression, Object value) {
        constants.put(expression, value);
    }

    public Object tryGetConstant(ExpressionNode expression) {
        if (constants.containsKey(expression))
            return constants.get(expression);

        return null;
    }

    public RuntimeType getType(ASTNode scope, String identifier) {
        while (scope != null) {
            var definedTypes = getTypesDefinedAt(scope);

            if (definedTypes.containsKey(identifier)) {
                return definedTypes.get(identifier);
            }

            if (!(scope instanceof ProgramNode) && scope.getParent() == null)
                throw new IllegalStateException(String.format("%s is supposed to have a parent but it does not.", scope.toString()));

            scope = scope.getParent();
        }

        return InvalidRuntimeType.instance;
    }
}
