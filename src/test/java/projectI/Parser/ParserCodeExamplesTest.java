package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Ignore;
import projectI.AST.*;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Primary.IntegralLiteralNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Statements.StatementNode;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static projectI.AST.ASTUtils.*;
import static projectI.AST.Primary.BooleanLiteralNode.falseLiteral;
import static projectI.AST.Primary.BooleanLiteralNode.trueLiteral;
import static projectI.Parser.ParserTestUtils.*;

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

        var expectedProgram = mainProgram(new StatementNode[] {
            integerDeclaration("a", null),
            integerAssignment("a", 1),
            implicitIntegerDeclaration("b", 2),
            implicitIntegerDeclaration("c", integerAddition("a", "b"))
        });

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testBadFormatting() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/bad_formatting.txt");

        var expectedProgram = mainProgram(new StatementNode[] {
            implicitIntegerDeclaration("a", 1),
            forLoop("i", 1, 10, new StatementNode[] {
                ifStatement(notEqual(mod("a", 2), 0),
                        assignment("a", integerAddition("a", "i"))),
            })
        });

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testArrayOfRecords() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_of_records.txt");

        var expectedProgram = programDeclaration(recordTypeDeclaration("rec",
                booleanDeclaration("either", null),
                integerDeclaration("num", null)),

                arrayTypeDeclaration("recordArray16", 16, new IdentifierNode("rec")),

                mainRoutine(typedVariable("arr", "recordArray16"),
                        typedVariable("myRec", "rec"),
                        recordMemberAssignment("myRec", "either", toExpression(trueLiteral)),
                        recordMemberAssignment("myRec", "num", toExpression(arraySize("arr"))),
                        arrayIndexAssignment("arr", 1, variableValue("myRec")))
        );

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testArrayWithBooleanSize() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_with_boolean_size.txt");

        var arraySize = new ExpressionNode(toRelation(trueLiteral))
                .addRelation(LogicalOperator.OR, toRelation(falseLiteral));

        var expectedProgram = programDeclaration(
                arrayTypeDeclaration("booleanArray", arraySize, new PrimitiveTypeNode(PrimitiveType.BOOLEAN)),
                mainRoutineReturningInteger(
                        typedVariable("arr", "booleanArray"),
                        arrayIndexAssignment("arr", 0, toExpression(trueLiteral)),
                        returnValue(toExpression(arraySize("arr")))
                )
        );

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
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

        var expectedProgram = programDeclaration(
                mainRoutine(
                        integerDeclaration("a"),
                        realDeclaration("b"),
                        booleanDeclaration("c"),

                        integerAssignment("a", 1),
                        realAssignment("a", 0.0),
                        booleanAssignment("a", true),

                        integerAssignment("b", 1),
                        realAssignment("b", 0.0),
                        booleanAssignment("b", false),

                        booleanAssignment("c", true),
                        integerAssignment("c", 1),

                        ifStatement(toExpression(new ModifiablePrimaryNode(new IdentifierNode("a"))),
                                booleanAssignment("c", false))
                )
        );

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testTypeSynonym() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/type_synonym.txt");

        var expectedProgram = programDeclaration(
                arrayTypeDeclaration("arr8", 8, integerType()),
                mainRoutine(
                        typedVariable("a", "arr8"),
                        arrayIndexAssignment("a", 0, toExpression(arraySize("a")))
                )
        );

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testWhileLoop() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/while_loop.txt");

        var comparison = new BinaryRelationNode(toSimple(new ModifiablePrimaryNode(new IdentifierNode("a"))),
                BinaryRelationNode.Comparison.LESS,
                toSimple(new IntegralLiteralNode(10)));

        var addition = new SimpleNode(toSummand(new ModifiablePrimaryNode(new IdentifierNode("a"))))
                .addSummand(AdditionOperator.PLUS, toSummand(new IntegralLiteralNode(1)));

        var expectedProgram = programDeclaration(
                mainRoutine(
                        implicitIntegerDeclaration("a", 0),

                        whileLoop(new ExpressionNode(comparison),
                                assignment("a", toExpression(addition))
                        )
                )
        );

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
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

    public void testRecords_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/records_invalid.txt");

        assertNull(program);
    }

    public void testForLoop() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/for_loop.txt");

        assertNotNull(program);
    }
}
