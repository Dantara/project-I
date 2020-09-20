package projectI.SemanticAnalysis;

import projectI.AST.Expressions.*;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class RoutineCallArgumentsAnalyzer extends ExpressionAnalyzer {
    private void analyze(RoutineCallNode routineCall, SymbolTable symbolTable) throws SemanticAnalysisException {
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

    @Override
    protected void analyze(ExpressionNode expression, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze(expression.relation, symbolTable);

        for (var otherRelation: expression.otherRelations) {
            analyze(otherRelation.node, symbolTable);
        }

        if (expression.parent instanceof RoutineCallNode) {
            analyze((RoutineCallNode) expression.parent, symbolTable);
        }
    }

    private void analyze(RelationNode relation, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (relation instanceof BinaryRelationNode) {
            var binaryRelation = (BinaryRelationNode) relation;
            analyze(binaryRelation.simple, symbolTable);

            if (binaryRelation.otherSimple != null)
                analyze(binaryRelation.otherSimple, symbolTable);

        } else if (relation instanceof NegatedRelationNode) {
            analyze(((NegatedRelationNode) relation).innerRelation, symbolTable);
        }
    }

    private void analyze(SimpleNode simple, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze(simple.summand, symbolTable);

        for (var otherSummand: simple.otherSummands) {
            analyze(otherSummand.node, symbolTable);
        }
    }

    private void analyze(SummandNode summand, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze(summand.factor, symbolTable);

        for (var otherFactor: summand.otherFactors) {
            analyze(otherFactor.node, symbolTable);
        }
    }

    private void analyze(FactorNode factor, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (factor instanceof RoutineCallNode) {
             analyze((RoutineCallNode) factor, symbolTable);
        }
    }
}
