package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.AST.Expressions.BinaryRelationNode;
import projectI.AST.Expressions.NegatedRelationNode;
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

    public void testReturnStatement() throws InvalidLexemeException {
        var position = createParser("   return a + 2").tryParseReturn(0, 4).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 3), position);
    }

    public void testRoutineCall() throws InvalidLexemeException {
        var position = createParser("  a(1, 2, 3)").tryParseRoutineCall(0, 8).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 2), position);
    }

    public void testExpression() throws InvalidLexemeException {
        var expression = createParser("1 and 2").tryParseExpression(0, 3);

        var positionOf1 = expression.getPosition();
        var positionOfAnd = expression.otherRelations.get(0).operatorPosition;

        assertNotNull(positionOf1);
        assertEquals(new CodePosition(0, 0), positionOf1);
        assertNotNull(positionOfAnd);
        assertEquals(new CodePosition(0, 2), positionOfAnd);
    }

    public void testBinaryRelation() throws InvalidLexemeException {
        var relation = (BinaryRelationNode) createParser("  1 < 2").tryParseRelation(0, 3);

        var positionOf1 = relation.getPosition();
        var positionOfLess = relation.comparisonPosition;

        assertNotNull(positionOf1);
        assertEquals(new CodePosition(0, 2), positionOf1);
        assertNotNull(positionOfLess);
        assertEquals(new CodePosition(0, 4), positionOfLess);
    }

    public void testNegatedRelation() throws InvalidLexemeException {
        var relation = (NegatedRelationNode) createParser("   not true").tryParseRelation(0, 2);
        var positionOfNot = relation.startPosition;

        assertNotNull(positionOfNot);
        assertEquals(new CodePosition(0, 3), positionOfNot);
    }

    public void testSimpleNode() throws InvalidLexemeException {
        var simple = createParser("  1+2").tryParseSimple(0, 3);

        var positionOfOne = simple.getPosition();
        var positionOfPlus = simple.otherSummands.get(0).operatorPosition;

        assertNotNull(positionOfOne);
        assertEquals(new CodePosition(0, 2), positionOfOne);
        assertNotNull(positionOfPlus);
        assertEquals(new CodePosition(0, 3), positionOfPlus);
    }

    public void testSummandNode() throws InvalidLexemeException {
        var summand = createParser("   1 * 3").tryParseSummand(0, 3);

        var positionOfOne = summand.getPosition();
        var positionOfProduct = summand.otherFactors.get(0).operatorPosition;

        assertNotNull(positionOfOne);
        assertEquals(new CodePosition(0, 3), positionOfOne);
        assertNotNull(positionOfProduct);
        assertEquals(new CodePosition(0, 5), positionOfProduct);
    }
}
