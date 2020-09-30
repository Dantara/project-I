package projectI;

import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.ProgramNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.CodeGeneration.JVM.JVMCodeGenerator;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;
import projectI.Parser.Parser;
import projectI.SemanticAnalysis.CompositeSemanticAnalyzer;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;
import projectI.SemanticAnalysis.SymbolTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws IOException, InvalidLexemeException {
        String fileName = getFileName(args);

        var file = new File(fileName);

        if (!file.exists()) {
            file = new File("code_examples/" + fileName);
        }

        if (file.exists()) {
            try {
                var sourceCode = Files.readString(file.toPath());
                var parser = createParserOf(sourceCode);
                var program = parser.tryParseProgram();

                if (program != null && program.validate()) {
                    System.out.println(program);
                    var symbolTable = new SymbolTable();

                    if (runSemanticAnalysis(program, symbolTable)) {
                        var generator = new JVMCodeGenerator(program, symbolTable);
                        byte[] bytes = generator.generate();
                        try (FileOutputStream stream = new FileOutputStream("target/Program.class")) {
                            stream.write(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    printParsingErrors(parser);
                }
            } catch (IOException e) {
                System.err.println("An error occurred when trying to read the file.");
                throw e;
            } catch (InvalidLexemeException e) {
                System.err.println("Lexical analyzer reported an error.");
                throw e;
            }
        } else {
            System.err.println("File does not exist.");
        }
    }

    private static String getFileName(String[] args) {
        String fileName;

        if (args.length == 1) {
            fileName = args[0];
        } else {
            System.out.print("Enter file name: ");
            var scanner = new Scanner(System.in);
            fileName = scanner.nextLine();
        }

        return fileName;
    }

    private static Parser createParserOf(String sourceCode) throws InvalidLexemeException {
        var lexer = new Lexer();
        var tokens = lexer.scan(sourceCode);
        return new Parser(tokens, lexer.getLexemesWithLocations());
    }

    private static void printParsingErrors(Parser parser) {
        System.err.printf("Parser reported %d error(s).\n",parser.getErrorCount());

        for (var error: parser.getErrors()) {
            System.err.println(error);
        }
    }

    private static boolean runSemanticAnalysis(ProgramNode program, SymbolTable symbolTable) {
        try {
            var printIntType = new RuntimeRoutineType(null);
            printIntType.parameters.add(new RuntimePrimitiveType(PrimitiveType.INTEGER));
            symbolTable.defineType(program, "printInt", printIntType);

            analyzer.analyze(program, symbolTable);
        } catch (SemanticAnalysisException e) {
            System.err.println("Semantic analysis not passed.");
            System.err.println(e.getMessage());
            return false;
        }

        System.out.println("Semantic analysis passed.");
        return true;
    }

    private final static CompositeSemanticAnalyzer analyzer = new CompositeSemanticAnalyzer();
}
