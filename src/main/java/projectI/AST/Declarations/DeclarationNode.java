package projectI.AST.Declarations;

import projectI.AST.ASTNode;
import projectI.CodePosition;

public abstract class DeclarationNode implements ASTNode {

    /**
     * Find start position in the source code
     * @return start position
     */
    public abstract CodePosition getStartPosition();
}
