package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.RecordTypeNode;
import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.Declarations.TypeDeclarationNode;
import projectI.AST.Declarations.VariableDeclarationNode;
import projectI.AST.ProgramNode;

public class VariableDeclarationAssignmentAnalyzer implements SemanticAnalyzer {

    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var declaration : program.declarations) {
            if (declaration instanceof VariableDeclarationNode) {
                analyze((VariableDeclarationNode) declaration, symbolTable);
            }

            if (declaration instanceof RoutineDeclarationNode) {
                analyze((RoutineDeclarationNode) declaration, symbolTable);
            }

            if (declaration instanceof TypeDeclarationNode) {
                var type = (TypeDeclarationNode) declaration;
                if (type.type instanceof RecordTypeNode) {
                    analyze((RecordTypeNode) type.type, symbolTable);
                }
            }
        }
    }

    private void analyze(RoutineDeclarationNode routine, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var statement : routine.body.statements) {
            if (statement instanceof VariableDeclarationNode) {
                analyze((VariableDeclarationNode) statement, symbolTable);
            }
        }
    }

    private void analyze(RecordTypeNode record, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var variable : record.variables) {
            analyze(variable, symbolTable);
        }
    }

    private void analyze(VariableDeclarationNode variableDeclaration, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (variableDeclaration.expression == null || variableDeclaration.type == null) return;
        var type = variableDeclaration.type.getType(symbolTable, variableDeclaration);

        if (!variableDeclaration.expression.getType().canBeCastedTo(type))
            throw new SemanticAnalysisException(this, variableDeclaration);
    }
}
