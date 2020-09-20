package projectI.SemanticAnalysis;

import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.ReturnStatementNode;

public class RoutineDeclarationAnalyzer implements SemanticAnalyzer {

    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var declaration : program.declarations) {
            if (!(declaration instanceof RoutineDeclarationNode)) continue;

            analyze((RoutineDeclarationNode) declaration, symbolTable);
        }
    }

    private void analyze(RoutineDeclarationNode routine, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var statement: routine.body.statements) {
            if (!(statement instanceof ReturnStatementNode)) continue;

            var returnStatement = (ReturnStatementNode) statement;
            if (routine.returnType == null && returnStatement.expression != null) {
                throw new SemanticAnalysisException(this, routine);
            }

            if (routine.returnType != null && returnStatement.expression == null) {
                throw new SemanticAnalysisException(this, routine);
            }

            if (routine.returnType != null) {
                var returnExpressionType = returnStatement.expression.getType(symbolTable);
                var routineReturnType = routine.returnType.getType(symbolTable);
                if (!returnExpressionType.canBeCastedTo(routineReturnType))
                    throw new IncompatibleTypesException(this, routine, routineReturnType, returnExpressionType);
            }
        }
    }
}
