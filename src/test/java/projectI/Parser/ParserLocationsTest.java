package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.CodePosition;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

public class ParserLocationsTest extends TestCase {
    public ParserLocationsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ParserLocationsTest.class);
    }

    private Parser createParser(String programText) throws InvalidLexemeException {
        var lexer = new Lexer();
        var tokens = lexer.scan(programText);
        return new Parser(tokens, lexer.getLexemesWithLocations());
    }

    public void testRealLiteralLocation_NoSign() throws InvalidLexemeException {
        var position = createParser("var a is 1.0").tryParseRealLiteral(3, 4).valuePosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 9), position);
    }

    public void testRealLiteralLocation_Sign() throws InvalidLexemeException {
        var position = createParser("var a is - 1.0").tryParseRealLiteral(3, 5).valuePosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 11), position);
    }

    public void testIntegerLiteralLocation_NoSign() throws InvalidLexemeException {
        var position = createParser("var a is 1").tryParseIntegralLiteral(3, 4).valuePosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 9), position);
    }

    public void testIntegerLiteralLocation_Sign() throws InvalidLexemeException {
        var position = createParser("var a is + 1").tryParseIntegralLiteral(3, 5).valuePosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 11), position);
    }

    public void testBooleanLiteralLocation() throws InvalidLexemeException {
        var position = createParser("\nvar a is true").tryParseBooleanLiteral(4, 5).position;

        assertNotNull(position);
        assertEquals(new CodePosition(1, 9), position);
    }

    public void testRange() throws InvalidLexemeException {
        var position = createParser("for i in 1..2").tryParseRange(2, 6).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 6), position);
    }

    public void testWhileLoop() throws InvalidLexemeException {
        var position = createParser("\n\n  while 1 < 2 loop end").tryParseWhileLoop(2, 8).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(2, 2), position);
    }

    public void testForLoop() throws InvalidLexemeException {
        var position = createParser("\n for i in 1..2 loop end").tryParseForLoop(1, 9).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(1, 1), position);
    }

    public void testIfStatement() throws InvalidLexemeException {
        var position = createParser("\n\n  if 1 then end").tryParseIfStatement(2, 6).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(2, 2), position);
    }

    public void testModifiablePrimary() throws InvalidLexemeException {
        var position = createParser("var n is a.size").tryParseModifiablePrimary(3, 6).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 9), position);
    }

    public void testAssignmentPosition() throws InvalidLexemeException {
        var position = createParser("\n    a:=1").tryParseAssignment(1, 4).getStartPosition();

        assertNotNull(position);
        assertEquals(new CodePosition(1, 4), position);
    }
}
