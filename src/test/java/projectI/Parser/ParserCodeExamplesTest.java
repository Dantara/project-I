package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.javatuples.Pair;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        return new Parser(tokens).tryParseProgram();
    }

    public void testBasic() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/basic.txt");

        var addition = new FactorNode(new ModifiablePrimaryNode(new IdentifierNode("a")));
        addition.otherSummands.add(
                new Pair<>(FactorNode.Operator.PLUS, new ModifiablePrimaryNode(new IdentifierNode("b"))));

        var body = new BodyNode()
                .add(new VariableDeclarationNode(new IdentifierNode("a"),
                        new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER),
                        null))
                .add(new AssignmentNode(new ModifiablePrimaryNode(new IdentifierNode("a")), ExpressionNode.integerLiteral(1)))
                .add(new VariableDeclarationNode(new IdentifierNode("b"),
                        null,
                        ExpressionNode.integerLiteral(2)))
                .add(new VariableDeclarationNode(new IdentifierNode("c"),
                        null,
                        new ExpressionNode(new BinaryRelationNode(new SimpleNode(addition)))));

        var routine = new RoutineDeclarationNode(new IdentifierNode("main"), new ParametersNode(), body);
        var expectedProgram = new ProgramNode()
                .add(routine);

        assertNotNull(program);
        assertEquals(expectedProgram, program);
    }

    public void testBadFormatting() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/bad_formatting.txt");

        assertNotNull(program);
    }

    public void testArrayOfRecords() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_of_records.txt");

        assertNotNull(program);
    }

    public void testArrayWithBooleanSize() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_with_boolean_size.txt");

        assertNotNull(program);
    }

    public void testArraysAndFor() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/arrays_and_for.txt");

        assertNotNull(program);
    }

    public void testConditional() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/conditional.txt");

        assertNotNull(program);
    }

    public void testDenseCode() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/dense_code.txt");

        assertNotNull(program);
    }

    public void testManyBrackets() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/many_brackets.txt");

        assertNotNull(program);
    }

    public void testNestedLoop() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/nested_loop.txt");

        assertNotNull(program);
    }

    public void testOperators() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/operators.txt");

        assertNotNull(program);
    }

    public void testRecords() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/records.txt");

        assertNotNull(program);
    }

    public void testRoutineCall() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/routine_call.txt");

        assertNotNull(program);
    }

    public void testTypeConversion() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/type_conversion.txt");

        assertNotNull(program);
    }

    public void testTypeSynonym() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/type_synonym.txt");

        assertNotNull(program);
    }

    public void testWhileLoop() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/while_loop.txt");

        assertNotNull(program);
    }
}
