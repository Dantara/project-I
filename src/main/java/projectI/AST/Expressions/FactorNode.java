package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.SymbolTable;

public interface FactorNode extends ASTNode {
    /**
     * Find a position in the source code
     * @return the position
     */
    CodePosition getPosition();
    Object tryEvaluateConstant();
    RuntimeType getType(SymbolTable symbolTable);
}
