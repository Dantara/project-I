package projectI.AST;

public interface ASTNode {
    ASTNode getParent();
    void setParent(ASTNode parent);
    boolean validate();
}
