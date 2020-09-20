package projectI.SemanticAnalysis;

import projectI.AST.Declarations.BodyNode;
import projectI.AST.Declarations.HasBody;
import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.StatementNode;

public class StatementParentsAnalyzer implements SemanticAnalyzer {
    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var declaration : program.declarations) {
            if (declaration instanceof RoutineDeclarationNode) {
                analyze((RoutineDeclarationNode) declaration, symbolTable);
            }
        }
    }

    private void analyze(HasBody hasBody, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (int index = 0; index < hasBody.getBodiesCount(); index++) {
            analyze(hasBody.getBody(index), symbolTable);
        }
    }

    private void analyze(BodyNode body, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var statement : body.statements) {
            analyze(statement, symbolTable);
        }
    }

    private void analyze(StatementNode statement, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (statement.getParent() == null)
            throw new SemanticAnalysisException(this, statement);

        if (statement instanceof HasBody)
            analyze((HasBody) statement, symbolTable);
    }
}
