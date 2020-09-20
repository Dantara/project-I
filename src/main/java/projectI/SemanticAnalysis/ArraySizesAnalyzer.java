package projectI.SemanticAnalysis;

import projectI.AST.Declarations.ArrayTypeNode;
import projectI.AST.Declarations.ParametersNode;
import projectI.SemanticAnalysis.Exceptions.ExpectedConstantException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public final class ArraySizesAnalyzer extends VisitorAnalyzer {
    @Override
    protected void analyze(ArrayTypeNode array, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(array, symbolTable);

        if (array.parent instanceof ParametersNode) {
            return;
        }

        if (array.size == null) {
            throw new SemanticAnalysisException(this, array);
        }

        var value = array.size.tryEvaluateConstant(symbolTable);
        if (value == null) {
            throw new ExpectedConstantException(this, array, array.size);
        }
    }
}
