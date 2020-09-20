package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

public interface TypeNode extends ASTNode {
    RuntimeType getType(SymbolTable symbolTable);
}
