package projectI.SemanticAnalysis;

import projectI.AST.Expressions.ExpressionNode;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class ConstantsCaching extends ExpressionAnalyzer {
    @Override
    protected void analyze(ExpressionNode expression, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (symbolTable.tryGetConstant(expression) != null) return;

        var constantValue = expression.tryEvaluateConstant(symbolTable);
        if (constantValue == null) return;

        symbolTable.defineConstant(expression, constantValue);
    }
}
