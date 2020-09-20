package projectI.SemanticAnalysis.Exceptions;

import projectI.AST.ASTNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.SemanticAnalysis.SemanticAnalyzer;

public class ExpectedConstantException extends SemanticAnalysisException {
    public final ExpressionNode expression;

    public ExpectedConstantException(SemanticAnalyzer analyzer, ASTNode node, ExpressionNode expression) {
        super(analyzer, node);
        this.expression = expression;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + String.format("\n Expected constant but got %s.", expression.toString());
    }
}
