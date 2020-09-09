package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.AST.*;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static projectI.AST.ASTUtils.integerExpression;
import static projectI.AST.ASTUtils.toExpression;

public class ParserCodeExamplesTest extends TestCase {
    public ParserCodeExamplesTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ParserCodeExamplesTest.class);
    }

    public static ProgramNode tryParseProgram(String path) throws IOException, InvalidLexemeException {
        var programText = Files.readString(Path.of(path));
        var lexer = new Lexer();
        var tokens = lexer.scan(programText);

        return new Parser(tokens, lexer.getLexemesWithLocations()).tryParseProgram();
    }

    public void testBasic() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/basic.txt");

        var addition = ASTUtils.toSimple(new ModifiablePrimaryNode(new IdentifierNode("a")))
                .addSummand(AdditionOperator.PLUS, new SummandNode(new ModifiablePrimaryNode(new IdentifierNode("b"))));

        var body = new BodyNode()
                .add(new VariableDeclarationNode(new IdentifierNode("a"),
                        new PrimitiveTypeNode(PrimitiveType.INTEGER), null))
                .add(new AssignmentNode(new ModifiablePrimaryNode(new IdentifierNode("a")), integerExpression(1)))
                .add(new VariableDeclarationNode(new IdentifierNode("b"), null, integerExpression(2)))
                .add(new VariableDeclarationNode(new IdentifierNode("c"),
                        null, toExpression(addition)));

        var routine = new RoutineDeclarationNode(new IdentifierNode("main"), new ParametersNode(), body);
        var expectedProgram = new ProgramNode()
                .addDeclaration(routine);

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testBadFormatting() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/bad_formatting.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testArrayOfRecords() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_of_records.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testArrayWithBooleanSize() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_with_boolean_size.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testArraysAndFor() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/arrays_and_for.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testConditional() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/conditional.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testDenseCode() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/dense_code.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testManyBrackets() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/many_brackets.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testNestedLoop() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/nested_loop.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testOperators() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/operators.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testRecords() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/records.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testRoutineCall() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/routine_call.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testTypeConversion() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/type_conversion.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testTypeSynonym() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/type_synonym.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testWhileLoop() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/while_loop.txt");

        assertNotNull(program);
        assertTrue(program.validate());
    }

    public void testConditional_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/conditional_invalid.txt");

        assertNull(program);
    }

    public void testArrayOfRecords_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_of_records_invalid.txt");

        assertNull(program);
    }

    public void testDenseCode_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/dense_code_invalid.txt");

        assertNull(program);
    }

    public void testManyBrackets_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/many_brackets_invalid.txt");

        assertNull(program);
    }

    public void testArraysAndFor_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/arrays_and_for_invalid.txt");

        assertNull(program);
    }
}
