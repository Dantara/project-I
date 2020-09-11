package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.AST.*;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.StatementNode;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static projectI.AST.ASTUtils.integerExpression;
import static projectI.AST.ASTUtils.toExpression;

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
                ifStatement(not_equal(mod("a", 2), 0), new StatementNode[] {
                    integerAssignment("a", integerAddition("a", "i")),
                }),
            })
        });

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testArrayOfRecords() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/array_of_records.txt");

        // var expectedProgram = programDeclaration(new DeclarationNode[] {

        //     recordTypeDeclaration("rec", new VariableDeclarationNode[] {
        //         booleanDeclaration("either", null),
        //         integerDeclaration("num", null)
        //     }),

        //     arrayTypeDeclaration("recordArray16", 16, new RecordTypeNode()),

        //     mainRoutine(new StatementNode[] {
                
        //     })
        // });
        assertNotNull(program);
        assertTrue(program.validate());
        // assertEquals(expectedProgram, program);
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

    public void testRecords_Invalid() throws IOException, InvalidLexemeException {
        var program = tryParseProgram("code_examples/records_invalid.txt");

        assertNull(program);
    }
}
