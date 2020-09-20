package projectI.SemanticAnalysis;

import projectI.AST.Declarations.VariableDeclarationNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class AssignmentAnalyzer extends VisitorAnalyzer {
    @Override
    protected void analyze(AssignmentNode assignment, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(assignment, symbolTable);

        var leftType = assignment.modifiable.getType(symbolTable);
        var rightType = assignment.assignedValue.getType(symbolTable);

        if (!rightType.canBeCastedTo(leftType))
            throw new IncompatibleTypesException(this, assignment, leftType, rightType);
    }

    @Override
    protected void analyze(VariableDeclarationNode variableDeclaration, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(variableDeclaration, symbolTable);

        if (variableDeclaration.expression == null || variableDeclaration.type == null) return;
        var variableType = variableDeclaration.type.getType(symbolTable);
        var variableAssignedType = variableDeclaration.expression.getType(symbolTable);

        if (!variableAssignedType.canBeCastedTo(variableType))
            throw new IncompatibleTypesException(this, variableDeclaration, variableType, variableAssignedType);
    }
}
