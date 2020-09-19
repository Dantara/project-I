package projectI.SemanticAnalysis;

import projectI.AST.ProgramNode;

public interface SemanticAnalyzer{
    void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException;
}
