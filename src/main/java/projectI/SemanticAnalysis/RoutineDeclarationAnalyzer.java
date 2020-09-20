package projectI.SemanticAnalysis;

import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class RoutineDeclarationAnalyzer extends VisitorAnalyzer {
    @Override
    protected void analyze(RoutineDeclarationNode routine, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(routine, symbolTable);
        
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
