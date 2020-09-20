package projectI.SemanticAnalysis;

import projectI.AST.ProgramNode;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public interface SemanticAnalyzer{
    void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException;
}
