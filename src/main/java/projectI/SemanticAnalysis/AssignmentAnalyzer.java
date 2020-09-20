package projectI.SemanticAnalysis;

import projectI.AST.Declarations.BodyNode;
import projectI.AST.Declarations.HasBody;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.AssignmentNode;

public class AssignmentAnalyzer implements SemanticAnalyzer {
    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var declaration : program.declarations) {
            if (declaration instanceof HasBody) {
                analyze((HasBody) declaration, symbolTable);
            }
        }
    }

    private void analyze(HasBody hasBody, SymbolTable symbolTable) throws SemanticAnalysisException  {
        for (int index = 0; index < hasBody.getBodiesCount(); index++) {
            analyze(hasBody.getBody(index), symbolTable);
        }
    }

    private void analyze(BodyNode body, SymbolTable symbolTable) throws SemanticAnalysisException  {
        for (var statement : body.statements) {
            if (statement instanceof HasBody) {
                analyze((HasBody) statement, symbolTable);
            } else if (statement instanceof AssignmentNode) {
                analyze((AssignmentNode) statement, symbolTable);
            }
        }
    }

    private void analyze(AssignmentNode assignment, SymbolTable symbolTable) throws SemanticAnalysisException  {
        var leftType = assignment.modifiable.getType(symbolTable);
        var rightType = assignment.assignedValue.getType(symbolTable);

        if (!rightType.canBeCastedTo(leftType))
            throw new IncompatibleTypesException(this, assignment, leftType, rightType);
    }
}
