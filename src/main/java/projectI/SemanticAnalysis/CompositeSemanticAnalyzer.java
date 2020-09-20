package projectI.SemanticAnalysis;

import projectI.AST.ProgramNode;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public final class CompositeSemanticAnalyzer implements SemanticAnalyzer {
    public void analyze(ProgramNode program) throws SemanticAnalysisException {
        analyze(program, new SymbolTable());
    }

    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var analyzer : analyzers) {
            analyzer.analyze(program, symbolTable);
        }
    }

    private final static SemanticAnalyzer[] analyzers = new SemanticAnalyzer[] {
            new ParentsAnalyzer(),
            new ConstantsCaching(),

            new SymbolTableConstructor(),

            new AssignmentAnalyzer(),
            new RoutineDeclarationAnalyzer(),
            new RoutineCallArgumentsAnalyzer(),
            new ArraySizesAnalyzer(),
            new LoopAnalyzer()
    };
}
