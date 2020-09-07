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
}
