package projectI.SemanticAnalysis;

import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class RoutineCallArgumentsAnalyzer extends VisitorAnalyzer {
    @Override
    protected void analyze(RoutineCallNode routineCall, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(routineCall, symbolTable);

        var type = symbolTable.getType(routineCall, routineCall.name.name);
        if (!(type instanceof RuntimeRoutineType))
            throw new SemanticAnalysisException(this, routineCall);

        var routine = (RuntimeRoutineType) type;
        if (routineCall.arguments.size() != routine.parameters.size())
            throw new SemanticAnalysisException(this, routineCall);

        for (var index = 0; index < routineCall.arguments.size(); index++) {
            var parameterType = routine.parameters.get(index);
            var argumentType = routineCall.arguments.get(index).getType(symbolTable);

            if (!argumentType.canBeCastedTo(parameterType))
                throw new IncompatibleTypesException(this, routineCall, parameterType, argumentType);
        }
    }
}
