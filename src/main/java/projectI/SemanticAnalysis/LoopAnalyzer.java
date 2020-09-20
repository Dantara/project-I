package projectI.SemanticAnalysis;

import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class LoopAnalyzer extends VisitorAnalyzer {
    @Override
    protected void analyze(WhileLoopNode whileLoop, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(whileLoop, symbolTable);

        var actualType = whileLoop.condition.getType(symbolTable);
        var expectedType = new RuntimePrimitiveType(PrimitiveType.BOOLEAN);
        if (!actualType.canBeCastedTo(expectedType))
            throw new IncompatibleTypesException(this, whileLoop, expectedType, actualType);
    }

    @Override
    protected void analyze(RangeNode range, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(range, symbolTable);

        analyzeRangeLimit(range.from, symbolTable);
        analyzeRangeLimit(range.to, symbolTable);
    }

    protected void analyzeRangeLimit(ExpressionNode limit, SymbolTable symbolTable) throws SemanticAnalysisException {
        var actualType = limit.getType(symbolTable);
        var expectedType = new RuntimePrimitiveType(PrimitiveType.INTEGER);
        if (!actualType.canBeCastedTo(expectedType))
            throw new IncompatibleTypesException(this, limit.parent, expectedType, actualType);
    }
}
