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

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
        return getOutput(sourceFileName, new String[0]);
    }

    private static String getOutput(String sourceFileName, String... input) throws Exception {
        var sourceCode = Files.readString(Path.of(sourceFileName));
        var lexer = new Lexer();
        var parser = new Parser(lexer.scan(sourceCode), lexer.getLexemesWithLocations());
        var program = parser.tryParseProgram();
        if (program == null)
            fail("Failed to parse the program.");

        var symbolTable = new SymbolTable();
        var compositeAnalyzer = new CompositeSemanticAnalyzer();
        compositeAnalyzer.analyze(program, symbolTable);
        var codeGenerator = new JVMCodeGenerator(program, symbolTable);
        var files = codeGenerator.generate();

        if (!Files.exists(Path.of("target", "tests")))
            Files.createDirectories(Path.of("target", "tests"));

        for (var className : files.keySet()) {
            var classFile = Path.of("target", "tests", className + ".class");

            if (Files.exists(classFile))
                Files.delete(classFile);

            Files.write(classFile, files.get(className), StandardOpenOption.CREATE_NEW);
        }

        var process = Runtime.getRuntime().exec("java -cp target/tests -noverify Program");

        if (input.length > 0) {
            var processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processInput.write(String.join(System.lineSeparator(), input));
            processInput.flush();
            processInput.close();
        }

        process.waitFor();

        InputStream stream = process.exitValue() == 0 ? process.getInputStream() : process.getErrorStream();

        try (var scanner = new Scanner(stream).useDelimiter("\\A")) {
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
        var output = getOutput("code_examples/factorial.txt", "5");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(120).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);

        output = getOutput("code_examples/factorial.txt", "3");
        expectedOutput = new StringBuilder();
        expectedOutput.append(6).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testNaiveSqrt() throws Exception {
        var output = getOutput("code_examples/naive_sqrt.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(5).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testSquareInput() throws Exception {
        var output = getOutput("code_examples/square_input.txt", "3");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(9).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);

        output = getOutput("code_examples/square_input.txt", "25");
        expectedOutput = new StringBuilder();
        expectedOutput.append(625).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testReadReal() throws Exception {
        var output = getOutput("code_examples/read_real.txt", "1,5");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(2.25).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testReadBoolean() throws Exception {
        var output = getOutput("code_examples/read_Boolean.txt", "true");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(false).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }


    public void testRecordDefinition() throws Exception {
        var output = getOutput("code_examples/record_definition.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(1).append(System.lineSeparator());
        expectedOutput.append(2).append(System.lineSeparator());
        expectedOutput.append(5).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testNestedRecord() throws Exception {
        var output = getOutput("code_examples/nested_record.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(4).append(System.lineSeparator());
        expectedOutput.append(1).append(System.lineSeparator());
        expectedOutput.append(2).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testCounter() throws Exception {
        var output = getOutput("code_examples/counter.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(1).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testRecords1() throws Exception {
        var output = getOutput("code_examples/records1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(-2.0).append(System.lineSeparator());
        expectedOutput.append(1.0).append(System.lineSeparator());
        expectedOutput.append(2.0).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testAnalogueTypes() throws Exception {
        var output = getOutput("code_examples/analogue_types.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(2).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testLocalArray() throws Exception {
        var output = getOutput("code_examples/local_array.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(10).append(System.lineSeparator());
        expectedOutput.append(2).append(System.lineSeparator());
        expectedOutput.append(5).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testArrayOfRecords1() throws Exception {
        var output = getOutput("code_examples/array_of_records1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(1).append(System.lineSeparator());
        expectedOutput.append(true).append(System.lineSeparator());
        expectedOutput.append(16).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testMultidimensionalArray() throws Exception {
        var output = getOutput("code_examples/multidimensional_array.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testGlobalArray() throws Exception {
        var output = getOutput("code_examples/global_array.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(5).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testRecordWithArray() throws Exception {
        var output = getOutput("code_examples/record_with_array.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(5).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testMatrix() throws Exception {
        var output = getOutput("code_examples/matrix.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(1.0).append(System.lineSeparator());
        expectedOutput.append(8.0).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testArrayWithoutSize() throws Exception {
        var output = getOutput("code_examples/array_without_size1.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(23).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testLinkedList() throws Exception {
        var output = getOutput("code_examples/linked_list.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(1).append(System.lineSeparator());
        expectedOutput.append(2).append(System.lineSeparator());
        expectedOutput.append(3).append(System.lineSeparator());
        expectedOutput.append(4).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }

    public void testNotOperator() throws Exception {
        var output = getOutput("code_examples/not_operator.txt");
        var expectedOutput = new StringBuilder();
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(1).append(System.lineSeparator());
        expectedOutput.append(0).append(System.lineSeparator());
        expectedOutput.append(1).append(System.lineSeparator());
        Assert.assertEquals(expectedOutput.toString(), output);
    }
}
