package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.CodePosition;

public abstract class DeclarationNode implements ASTNode {
    public abstract CodePosition getStartPosition();
}
