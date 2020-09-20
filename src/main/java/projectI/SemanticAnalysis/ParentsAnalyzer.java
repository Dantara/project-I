package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.ProgramNode;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class ParentsAnalyzer extends VisitorAnalyzer {
    @Override
    protected void analyze(ASTNode node, SymbolTable symbolTable) throws SemanticAnalysisException {
        super.analyze(node, symbolTable);

        if (node instanceof ProgramNode) {
            if (node.getParent() != null)
                throw new SemanticAnalysisException(this, node);
        } else {
            if (node.getParent() == null)
                throw new SemanticAnalysisException(this, node);
        }
    }
}
