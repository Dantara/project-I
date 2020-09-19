package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;

public class UndefinedSymbolException extends SemanticAnalysisException {
    public final String identifier;
    public final SymbolTable symbolTable;

    public UndefinedSymbolException(SymbolTable symbolTable, ASTNode node, String identifier) {
        super(null, node);
        this.symbolTable = symbolTable;
        this.identifier = identifier;
    }

    @Override
    public String getMessage() {
        return identifier + " is not defined.";
    }
}