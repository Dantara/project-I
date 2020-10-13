package projectI.SemanticAnalysis;

import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;
import projectI.AST.Statements.StatementNode;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.Flow.IfStatementNode;
import java.util.List;

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

        if (!isReturnExistChecker(routine.body.statements) && routine.returnType != null){
            throw new SemanticAnalysisException(this, routine.returnType);
        }
    }

    private boolean isReturnExistChecker(List<StatementNode> statements) {
        boolean isReturnExist = false;

        for (var statement: statements){
            if (statement instanceof ReturnStatementNode) {
                return true;
            }

            if(statement instanceof ForLoopNode) {
                isReturnExist = isReturnExist
                    || isReturnExistChecker((ForLoopNode) statement);
            }


            if(statement instanceof WhileLoopNode) {
                isReturnExist = isReturnExist
                    || isReturnExistChecker((WhileLoopNode) statement);
            }


            if(statement instanceof IfStatementNode) {
                isReturnExist = isReturnExist
                    || isReturnExistChecker((IfStatementNode) statement);
            }
        }

        return isReturnExist;
    }

    private boolean isReturnExistChecker(ForLoopNode loopNode) {
        return isReturnExistChecker(loopNode.body.statements);
    }

    private boolean isReturnExistChecker(WhileLoopNode loopNode) {
        return isReturnExistChecker(loopNode.body.statements);
    }

    private boolean isReturnExistChecker(IfStatementNode ifNode) {
        var body = ifNode.body;
        var elseBody = ifNode.elseBody;

        if (body != null && elseBody != null){
            return isReturnExistChecker(body.statements)
                || isReturnExistChecker(elseBody.statements);
        }

        if (body != null){
            return isReturnExistChecker(body.statements);
        }

        if (elseBody != null){
            return isReturnExistChecker(elseBody.statements);
        }

        return false;
    }
}

