package projectI.CodeGeneration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import projectI.AST.ProgramNode;
import projectI.CodeGeneration.JVM.JVMCodeGenerator;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;
import projectI.Parser.Parser;
import projectI.SemanticAnalysis.CompositeSemanticAnalyzer;
import projectI.SemanticAnalysis.SymbolTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class JVMCodeGenerationTest extends TestCase {
    public JVMCodeGenerationTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(JVMCodeGenerationTest.class);
    }

    public static ProgramNode tryParseProgram(String path) throws IOException, InvalidLexemeException {
        var programText = Files.readString(Path.of(path));
        var lexer = new Lexer();
        var tokens = lexer.scan(programText);

        return new Parser(tokens, lexer.getLexemesWithLocations()).tryParseProgram();
    }

    private CompositeSemanticAnalyzer analyzer;

    private static String getOutput(String sourceFileName) throws Exception {
        var sourceCode = Files.readString(Path.of(sourceFileName));
        var lexer = new Lexer();
        var parser = new Parser(lexer.scan(sourceCode), lexer.getLexemesWithLocations());
        var program = parser.tryParseProgram();
        var symbolTable = new SymbolTable();
        var compositeAnalyzer = new CompositeSemanticAnalyzer();
        compositeAnalyzer.analyze(program, symbolTable);
        var codeGenerator = new JVMCodeGenerator(program, symbolTable);
        var bytes = codeGenerator.generate();
        var classFile = Path.of("target", "tests", "Program.class");
        if (!Files.exists(Path.of("target", "tests")))
            Files.createDirectories(Path.of("target", "tests"));
        if (Files.exists(classFile))
            Files.delete(classFile);
        Files.write(classFile, bytes, StandardOpenOption.CREATE_NEW);

        var process = Runtime.getRuntime().exec("java -cp target/tests -noverify Program >> target/tests/result.txt");
        var inputStream = process.getInputStream();

        try (var scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public void testBasic1() throws Exception {
        var output = getOutput("code_examples/basic1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(3).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testManyBrackets1() throws Exception {
        var output = getOutput("code_examples/many_brackets1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(36).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(4.7).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testOperators1() throws Exception {
        var output = getOutput("code_examples/operators1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(75.0).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(false).append(System.lineSeparator());
        expectedOutput.append(false).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(false).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }
}