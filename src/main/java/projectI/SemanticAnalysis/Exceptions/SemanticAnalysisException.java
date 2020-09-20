package projectI.SemanticAnalysis.Exceptions;

import projectI.AST.ASTNode;
import projectI.SemanticAnalysis.SemanticAnalyzer;

public class SemanticAnalysisException extends Exception {
    public final SemanticAnalyzer analyzer;
    public final ASTNode node;

    public SemanticAnalysisException(SemanticAnalyzer analyzer, ASTNode node) {
        this.analyzer = analyzer;
        this.node = node;
    }

    @Override
    public String getMessage() {
        return analyzer.getClass().getSimpleName() + " has found an error at " + node;
    }
}
