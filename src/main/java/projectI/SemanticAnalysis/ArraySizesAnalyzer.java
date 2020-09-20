package projectI.SemanticAnalysis;

import projectI.AST.Declarations.ArrayTypeNode;
import projectI.AST.Declarations.ParametersNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.SemanticAnalysis.Exceptions.ExpectedConstantException;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
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

        var sizeType = array.size.getType(symbolTable);
        var integerType = new RuntimePrimitiveType(PrimitiveType.INTEGER);
        if (!sizeType.canBeCastedTo(integerType))
            throw new IncompatibleTypesException(this, array, integerType, sizeType);
    }
}
