package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.CodePosition;

public interface RelationNode extends ASTNode {
    /**
     * Find a position in the source code
     * @return the position
     */
    CodePosition getPosition();
}
