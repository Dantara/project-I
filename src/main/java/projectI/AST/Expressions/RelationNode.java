package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.CodePosition;

public interface RelationNode extends ASTNode {
    CodePosition getPosition();
}
