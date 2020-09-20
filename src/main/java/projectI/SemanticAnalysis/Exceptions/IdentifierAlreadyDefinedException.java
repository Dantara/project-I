package projectI.SemanticAnalysis.Exceptions;

import projectI.AST.ASTNode;
import projectI.SemanticAnalysis.SymbolTable;

public class IdentifierAlreadyDefinedException extends SemanticAnalysisException{
    public final String identifier;
    public final SymbolTable symbolTable;

    public IdentifierAlreadyDefinedException(SymbolTable symbolTable, ASTNode node, String identifier) {
        super(null, node);
        this.symbolTable = symbolTable;
        this.identifier = identifier;
    }

    @Override
    public String getMessage() {
        return identifier + " is already defined.";
    }
}
