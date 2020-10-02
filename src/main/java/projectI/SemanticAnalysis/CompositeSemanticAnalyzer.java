package projectI.SemanticAnalysis;

import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.ProgramNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public final class CompositeSemanticAnalyzer implements SemanticAnalyzer {
    public void analyze(ProgramNode program) throws SemanticAnalysisException {
        analyze(program, new SymbolTable());
    }

    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        var printIntType = new RuntimeRoutineType(null);
        printIntType.parameters.add(new RuntimePrimitiveType(PrimitiveType.INTEGER));
        symbolTable.defineType(program, "printInt", printIntType);

        var printBoolType = new RuntimeRoutineType(null);
        printBoolType.parameters.add(new RuntimePrimitiveType(PrimitiveType.BOOLEAN));
        symbolTable.defineType(program, "printBoolean", printBoolType);

        var printRealType = new RuntimeRoutineType(null);
        printRealType.parameters.add(new RuntimePrimitiveType(PrimitiveType.REAL));
        symbolTable.defineType(program, "printReal", printRealType);

        var readInt = new RuntimeRoutineType(new RuntimePrimitiveType(PrimitiveType.INTEGER));
        symbolTable.defineType(program, "readInt", readInt);

        if (program == null)
            throw new IllegalArgumentException("Program cannot be null.");

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
