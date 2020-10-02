package projectI.CodeGeneration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import projectI.CodeGeneration.JVM.JVMCodeGenerator;
import projectI.Lexer.Lexer;
import projectI.Parser.Parser;
import projectI.SemanticAnalysis.CompositeSemanticAnalyzer;
import projectI.SemanticAnalysis.SymbolTable;

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

    private static String getOutput(String sourceFileName) throws Exception {
        return getOutput(sourceFileName, "");
    }

    private static String getOutput(String sourceFileName, String input) throws Exception {
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

        var process = Runtime.getRuntime().exec("java -cp target/tests -noverify Program");

        if (input.length() > 0) {
            var processInput = process.getOutputStream();
            processInput.write(input.getBytes());
            processInput.close();
        }

        var processOutput = process.getInputStream();

        try (var scanner = new Scanner(processOutput).useDelimiter("\\A")) {
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

    public void testRoutineCall1() throws Exception {
        var output = getOutput("code_examples/routine_call1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(3).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testConditional1() throws Exception {
        var output = getOutput("code_examples/conditional1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(1.0).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testForLoop() throws Exception {
        var output = getOutput("code_examples/for_loop.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(1).append(System.lineSeparator());
        expectedOutput.append(2).append(System.lineSeparator());
        expectedOutput.append(3).append(System.lineSeparator());
        expectedOutput.append(4).append(System.lineSeparator());
        expectedOutput.append(5).append(System.lineSeparator());
        expectedOutput.append(3).append(System.lineSeparator());
        expectedOutput.append(2).append(System.lineSeparator());
        expectedOutput.append(1).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testWhileLoop1() throws Exception {
        var output = getOutput("code_examples/while_loop1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(10).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testEvenNumbersSum() throws Exception {
        var output = getOutput("code_examples/even_numbers_sum.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(2 + 4 + 6 + 8 + 10).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testFactorial() throws Exception {
        var output = getOutput("code_examples/factorial.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(120).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testNaiveSqrt() throws Exception {
        var output = getOutput("code_examples/naive_sqrt.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(5).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testSquareInput() throws Exception {
        var output = getOutput("code_examples/square_input.txt", "25");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(625).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }
}
