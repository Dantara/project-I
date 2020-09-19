package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;

public class SemanticAnalysisException extends Exception {
    public final SemanticAnalyzer analyzer;
    public final ASTNode node;

    public SemanticAnalysisException(SemanticAnalyzer analyzer, ASTNode node) {
        this.analyzer = analyzer;
        this.node = node;
    }

    @Override
    public String getMessage() {
        return analyzer + " has failed at " + node;
    }
}
