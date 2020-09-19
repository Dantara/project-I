package projectI.SemanticAnalysis;

import projectI.AST.ProgramNode;

public final class CompositeSemanticAnalyzer implements SemanticAnalyzer {
    public void analyze(ProgramNode program) throws SemanticAnalysisException {
        analyze(program, new SymbolTable());
    }

    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {for (var analyzer : analyzers) {
            analyzer.analyze(program, symbolTable);
        }
    }

    private final static SemanticAnalyzer[] analyzers = new SemanticAnalyzer[] {
            new SymbolTableConstructor(),
            new VariableDeclarationAssignmentAnalyzer(),
            new RoutineAnalyzer()
    };
}
