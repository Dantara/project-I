package projectI.AST.Declarations;

public interface HasBody {
    BodyNode getBody(int index);
    int getBodiesCount();
}
