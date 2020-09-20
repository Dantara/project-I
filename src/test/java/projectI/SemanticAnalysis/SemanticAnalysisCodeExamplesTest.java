package projectI.SemanticAnalysis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.AST.*;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;
import projectI.Parser.Parser;
import projectI.SemanticAnalysis.Exceptions.ExpectedConstantException;
import projectI.SemanticAnalysis.Exceptions.IncompatibleTypesException;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SemanticAnalysisCodeExamplesTest extends TestCase {
    public SemanticAnalysisCodeExamplesTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SemanticAnalysisCodeExamplesTest.class);
    }

    public static ProgramNode tryParseProgram(String path) throws IOException, InvalidLexemeException {
        var programText = Files.readString(Path.of(path));
        var lexer = new Lexer();
        var tokens = lexer.scan(programText);

        return new Parser(tokens, lexer.getLexemesWithLocations()).tryParseProgram();
    }

    private CompositeSemanticAnalyzer analyzer;

    @Override
    protected void setUp() {
        analyzer = new CompositeSemanticAnalyzer();
    }

    public void testBasic() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/basic.txt");
        analyzer.analyze(program);
    }

    public void testArrayOfRecords() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/array_of_records.txt");
        analyzer.analyze(program);
    }

    public void testWithBooleanSize() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/array_with_boolean_size.txt");
        analyzer.analyze(program);
    }

    public void testArraysAndFor() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/arrays_and_for.txt");
        analyzer.analyze(program);
    }

    public void testBadFormatting() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/bad_formatting.txt");
        analyzer.analyze(program);
    }

    public void testConditional() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/conditional.txt");
        analyzer.analyze(program);
    }

    public void testDenseCode() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/dense_code.txt");
        analyzer.analyze(program);
    }

    public void testManyBrackets() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/many_brackets.txt");
        analyzer.analyze(program);
    }

    public void testNestedLoop() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/nested_loop.txt");
        analyzer.analyze(program);
    }

    public void testOperators() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/operators.txt");
        analyzer.analyze(program);
    }

    public void testRecords() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/records.txt");
        analyzer.analyze(program);
    }

    public void testRoutineCall() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/routine_call.txt");
        analyzer.analyze(program);
    }

    public void testTypeConversion() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/type_conversion.txt");
        analyzer.analyze(program);
    }

    public void testTypeSynonym() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/type_synonym.txt");
        analyzer.analyze(program);
    }

    public void testWhileLoop() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        var program = tryParseProgram("code_examples/while_loop.txt");
        analyzer.analyze(program);
    }

    public void testVoidRoutineAssignment_Invalid() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        try {
            var program = tryParseProgram("code_examples/void_routine_assignment_invalid.txt");
            analyzer.analyze(program);
            fail();
        } catch (IncompatibleTypesException ignored) {

        }
    }

    public void testAssignmentTypeMismatch_Invalid() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        try {
            var program = tryParseProgram("code_examples/assignment_type_mismatch_invalid.txt");
            analyzer.analyze(program);
            fail();
        } catch (IncompatibleTypesException ignored) {

        }
    }

    public void testNonConstantArraySize_Invalid() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        try {
            var program = tryParseProgram("code_examples/non_constant_array_size_invalid.txt");
            analyzer.analyze(program);
            fail();
        } catch (ExpectedConstantException ignored) {

        }
    }

    public void testRange_Invalid() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        try {
            var program = tryParseProgram("code_examples/range_invalid.txt");
            analyzer.analyze(program);
            fail();
        } catch (IncompatibleTypesException ignored) {

        }
    }

    public void testWhileLoopCondition_Invalid() throws IOException, InvalidLexemeException, SemanticAnalysisException {
        try {
            var program = tryParseProgram("code_examples/while_condition_invalid.txt");
            analyzer.analyze(program);
            fail();
        } catch (IncompatibleTypesException ignored) {

        }
    }
}
