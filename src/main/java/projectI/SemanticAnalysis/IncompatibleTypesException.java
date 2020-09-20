package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimeType;

public class IncompatibleTypesException extends SemanticAnalysisException {
    public final RuntimeType leftType;
    public final RuntimeType rightType;

    public IncompatibleTypesException(SemanticAnalyzer analyzer, ASTNode node, RuntimeType leftType, RuntimeType rightType) {
        super(analyzer, node);
        this.leftType = leftType;
        this.rightType = rightType;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + String.format("\n %s cannot be casted to %s.", rightType.toString(), leftType.toString());
    }
}
