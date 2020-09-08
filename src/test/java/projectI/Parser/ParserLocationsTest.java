package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.Declarations.VariableDeclarationNode;
import projectI.AST.Expressions.BinaryRelationNode;
import projectI.AST.Expressions.NegatedRelationNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.RoutineCallNode;
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

    public void testArrayType() throws InvalidLexemeException {
        var position = createParser("\n  array[1] integer").tryParseArrayType(1, 6).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(1, 2), position);
    }

    public void testVariableDeclaration() throws InvalidLexemeException {
        var variable = createParser("\n var a is 1").tryParseVariableDeclaration(1, 5);
        var position = variable.startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(1, 1), position);
        assertEquals(new CodePosition(1, 5), variable.identifier.position);
    }

    public void testTypeDeclaration() throws InvalidLexemeException {
        var position = createParser("\n  type int is integer").tryParseTypeDeclaration(1, 5).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(1, 2), position);
    }

    public void testBody() throws InvalidLexemeException {
        var body = createParser(" a := 1; b := 2").tryParseBody(0, 7);

        var positionOfA = body.statements.get(0).getStartPosition();
        var positionOfB = body.statements.get(1).getStartPosition();

        assertNotNull(positionOfA);
        assertEquals(new CodePosition(0, 1), positionOfA);
        assertNotNull(positionOfB);
        assertEquals(new CodePosition(0, 9), positionOfB);
    }

    public void testIdentifier() throws InvalidLexemeException {
        var position = createParser("\n\narr").tryParseIdentifier(2, 3).position;

        assertNotNull(position);
        assertEquals(new CodePosition(2, 0), position);
    }

    public void testParameters_Empty() throws InvalidLexemeException {
        var position = createParser("  ").tryParseParameters(0, 0).startPosition;

        assertNull(position);
    }

    public void testParameters() throws InvalidLexemeException {
        var parameters = createParser("(a:integer, b:real)").tryParseParameters(1, 8);

        var position = parameters.startPosition;
        var positionOfA = parameters.parameters.get(0).getValue0().position;
        var positionOfB = parameters.parameters.get(1).getValue0().position;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 1), position);
        assertNotNull(positionOfA);
        assertEquals(new CodePosition(0, 1), positionOfA);
        assertNotNull(positionOfB);
        assertEquals(new CodePosition(0, 12), positionOfB);
    }

    public void testPrimitiveType() throws InvalidLexemeException {
        var position = createParser(" integer").tryParsePrimitiveType(0, 1).position;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 1), position);
    }

    public void testRecordType() throws InvalidLexemeException {
        var record = createParser("record\n    var a is 1\n    var b is 2\nend").tryParseRecordType(0, 13);

        var firstVarPosition = record.variables.get(0).startPosition;
        var secondVarPosition = record.variables.get(1).startPosition;

        assertNotNull(record.startPosition);
        assertEquals(new CodePosition(0, 0), record.startPosition);
        assertNotNull(firstVarPosition);
        assertEquals(new CodePosition(1, 4), firstVarPosition);
        assertNotNull(secondVarPosition);
        assertEquals(new CodePosition(2, 4), secondVarPosition);
    }

    public void testRoutineDeclaration() throws InvalidLexemeException {
        var position = createParser(" routine main() is end").tryParseRoutineDeclaration(0, 6).startPosition;

        assertNotNull(position);
        assertEquals(new CodePosition(0, 1), position);
    }

    public void testProgram() throws InvalidLexemeException {
        var program = createParser("var a is 1\nroutine main() is\n    a := a + 1\n    print(a)\nend").tryParseProgram();

        var variable = (VariableDeclarationNode) program.declarations.get(0);
        assertEquals(new CodePosition(0, 0), variable.startPosition);
        assertEquals(new CodePosition(0, 4), variable.identifier.position);
        assertEquals(new CodePosition(0, 9), variable.expression.getPosition());

        var routine = (RoutineDeclarationNode) program.declarations.get(1);
        assertEquals(new CodePosition(1, 0), routine.startPosition);
        assertEquals(new CodePosition(1, 13), routine.parameters.startPosition);

        var assignment = (AssignmentNode) routine.body.statements.get(0);
        assertEquals(new CodePosition(2, 4), assignment.getStartPosition());
        assertEquals(new CodePosition(2, 4), assignment.modifiable.startPosition);
        assertEquals(new CodePosition(2, 9), assignment.assignedValue.getPosition());

        var routineCall = (RoutineCallNode) routine.body.statements.get(1);
        assertEquals(new CodePosition(3, 4), routineCall.getStartPosition());
        assertEquals(new CodePosition(3, 4), routineCall.name.position);
        assertEquals(new CodePosition(3, 10), routineCall.arguments.get(0).getPosition());
    }
}
