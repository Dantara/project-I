package projectI.AST.Statements;

import projectI.AST.ASTNode;
import projectI.CodePosition;

public interface StatementNode extends ASTNode {
    CodePosition getStartPosition();
}
