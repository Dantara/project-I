package projectI.SemanticAnalysis;

import projectI.AST.Declarations.ArrayTypeNode;
import projectI.AST.Declarations.ParametersNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.SemanticAnalysis.Exceptions.ExpectedConstantException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public final class ArraySizesAnalyzer extends ExpressionAnalyzer {
    @Override
    protected void analyze(ExpressionNode expression, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (expression.parent instanceof ArrayTypeNode) {
            var array = (ArrayTypeNode) expression.parent;
            if (array.parent instanceof ParametersNode) {
                return;
            }

            if (array.size == null) {
                throw new SemanticAnalysisException(this, array);
            }

            var value = array.size.tryEvaluateConstant();
            if (value == null) {
                throw new ExpectedConstantException(this, array, array.size);
            }
        }
    }
}
