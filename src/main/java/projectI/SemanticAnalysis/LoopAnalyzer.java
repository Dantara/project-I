package projectI.SemanticAnalysis;

import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class LoopAnalyzer extends ExpressionAnalyzer {
    @Override
    protected void analyze(ExpressionNode expression, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (expression.parent == null)
            throw new IllegalStateException("Expression must have a parent.");

        if (expression.parent instanceof RangeNode) {
            var actualType = expression.getType(symbolTable);
            var expectedType = new RuntimePrimitiveType(PrimitiveType.INTEGER);
            if (!actualType.canBeCastedTo(expectedType))
                throw new IncompatibleTypesException(this, expression.parent, expectedType, actualType);
        }

        if (expression.parent instanceof WhileLoopNode) {
            var loop = (WhileLoopNode) expression.parent;
            if (expression == loop.condition) {
                var actualType = expression.getType(symbolTable);
                var expectedType = new RuntimePrimitiveType(PrimitiveType.BOOLEAN);
                if (!actualType.canBeCastedTo(expectedType))
                    throw new IncompatibleTypesException(this, loop, expectedType, actualType);
            }
        }
    }
}
