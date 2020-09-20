package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Statements.StatementNode;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public abstract class ExpressionAnalyzer implements SemanticAnalyzer {
    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var declaration : program.declarations) {
            if (declaration instanceof RoutineDeclarationNode) {
                analyze((RoutineDeclarationNode) declaration, symbolTable);
            }

            if (declaration instanceof HasBody) {
                analyze((HasBody) declaration, symbolTable);
            }

            if (declaration instanceof SimpleDeclarationNode) {
                analyze((SimpleDeclarationNode) declaration, symbolTable);
            }
        }
    }

    private void analyze(RoutineDeclarationNode routine, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var parameter : routine.parameters.parameters) {
            analyze(parameter.getValue1(), symbolTable);
        }
    }

    private void analyze(HasBody hasBody, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (int index = 0; index < hasBody.getBodiesCount(); index++) {
            analyze(hasBody.getBody(index), symbolTable);
        }
    }

    private void analyze(BodyNode node, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var statement : node.statements) {
            analyze(statement, symbolTable);
        }
    }

    private void analyze(StatementNode statement, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (statement instanceof SimpleDeclarationNode) {
            analyze((SimpleDeclarationNode) statement, symbolTable);
        } else if (statement instanceof RoutineCallNode) {
            analyze((RoutineCallNode) statement, symbolTable);
        } else if (statement instanceof AssignmentNode) {
            var assignment = (AssignmentNode) statement;
            analyze(assignment.assignedValue, symbolTable);
        } else if (statement instanceof ForLoopNode) {
            var loop = (ForLoopNode) statement;
            analyze(loop.range.from, symbolTable);
            analyze(loop.range.to, symbolTable);
        } else if (statement instanceof WhileLoopNode) {
            var loop = (WhileLoopNode) statement;
            analyze(loop.condition, symbolTable);
        } else if (statement instanceof IfStatementNode) {
            var ifStatement = (IfStatementNode) statement;
            analyze(ifStatement.condition, symbolTable);
        } else if (statement instanceof ReturnStatementNode) {
            var returnStatement = (ReturnStatementNode) statement;

            if (returnStatement.expression != null)
                analyze(returnStatement.expression, symbolTable);
            else
                analyzeNullExpression(returnStatement, symbolTable);
        }

        if (statement instanceof HasBody)
            analyze((HasBody) statement, symbolTable);
    }

    private void analyze(SimpleDeclarationNode simpleDeclaration, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (simpleDeclaration instanceof VariableDeclarationNode) {
            var variable = (VariableDeclarationNode) simpleDeclaration;
            if (variable.expression != null)
                analyze(variable.expression, symbolTable);
            else
                analyzeNullExpression(variable, symbolTable);
        } else if (simpleDeclaration instanceof TypeDeclarationNode) {
            var type = (TypeDeclarationNode) simpleDeclaration;
            analyze(type.type, symbolTable);
        }
    }

    private void analyze(TypeNode type, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (type instanceof ArrayTypeNode) {
            var array = (ArrayTypeNode) type;

            if (array.size != null)
                analyze(array.size, symbolTable);
            else
                analyzeNullExpression(type, symbolTable);

            analyze(array.elementType, symbolTable);
        } else if (type instanceof RecordTypeNode) {
            var record = (RecordTypeNode) type;

            for (var variable : record.variables) {
                analyze(variable, symbolTable);
            }
        }
    }

    private void analyze(RoutineCallNode routineCall, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var index = 0; index < routineCall.arguments.size(); index++) {
            analyze(routineCall.arguments.get(index), symbolTable);
        }
    }

    protected abstract void analyze(ExpressionNode expression, SymbolTable symbolTable) throws SemanticAnalysisException;
    protected  void analyzeNullExpression(ASTNode parent, SymbolTable symbolTable) throws SemanticAnalysisException {

    }
}
