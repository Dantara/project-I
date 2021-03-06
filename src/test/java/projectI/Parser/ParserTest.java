package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.Primary.IntegralLiteralNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Primary.RealLiteralNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

import static projectI.AST.ASTUtils.*;
import static projectI.AST.Primary.BooleanLiteralNode.trueLiteral;

public class ParserTest extends TestCase {
    public ParserTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ParserTest.class);
    }

    private static ProgramNode parse(String programText) throws InvalidLexemeException {
        return createParser(programText).tryParseProgram();
    }

    private static Parser createParser(String programText) throws InvalidLexemeException {
        var lexer = new Lexer();
        var tokens = lexer.scan(programText);
        return new Parser(tokens, lexer.getLexemesWithLocations());
    }

    public void testEmptyProgram() throws InvalidLexemeException {
        var program = parse("");

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(0, program.declarations.size());
    }

    public void testIdentifier() throws InvalidLexemeException {
        var identifier = createParser("name").tryParseIdentifier(0, 1);

        assertNotNull(identifier);
        assertTrue(identifier.validate());
        assertEquals(new IdentifierNode("name"), identifier);
    }

    public void testPrimitiveType() throws InvalidLexemeException {
        var primitiveType = createParser("integer").tryParsePrimitiveType(0, 1);

        assertNotNull(primitiveType);
        assertTrue(primitiveType.validate());
        assertEquals(new PrimitiveTypeNode(PrimitiveType.INTEGER), primitiveType);
    }

    public void testType() throws InvalidLexemeException {
        var primitiveType = createParser("integer").tryParseType(0, 1);

        assertNotNull(primitiveType);
        assertTrue(primitiveType.validate());
        assertEquals(new PrimitiveTypeNode(PrimitiveType.INTEGER), primitiveType);
    }

    public void testExpressionLiteral() throws InvalidLexemeException {
        var expression = createParser("1").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(integerExpression(1), expression);
    }

    public void testIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseIntegralLiteral(0, 1);

        assertNotNull(literal);
        assertTrue(literal.validate());
        assertEquals(new IntegralLiteralNode(1), literal);
    }

    public void testPrimaryIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParsePrimary(0, 1);

        assertNotNull(literal);
        assertTrue(literal.validate());
        assertEquals(new IntegralLiteralNode(1), literal);
    }

    public void testSummandIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseFactor(0, 1);

        assertNotNull(literal);
        assertTrue(literal.validate());
        assertEquals(new IntegralLiteralNode(1), literal);
    }

    public void testFactorIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseSummand(0, 1);

        assertNotNull(literal);
        assertTrue(literal.validate());
        assertEquals(new SummandNode(new IntegralLiteralNode(1)), literal);
    }

    public void testSimpleIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseSimple(0, 1);

        assertNotNull(literal);
        assertTrue(literal.validate());
        assertEquals(new SimpleNode(new SummandNode(new IntegralLiteralNode(1))), literal);
    }

    public void testRelationIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseRelation(0, 1);

        assertNotNull(literal);
        assertTrue(literal.validate());
        assertEquals(new BinaryRelationNode(new SimpleNode(new SummandNode(new IntegralLiteralNode(1)))), literal);
    }

    public void testModifiablePrimaryMember() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a.a").tryParseModifiablePrimary(0, 3);

        assertNotNull(modifiablePrimary);
        assertTrue(modifiablePrimary.validate());
        assertEquals(1, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addMember(new IdentifierNode("a"));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryMembersSequence() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a.a.a").tryParseModifiablePrimary(0, 5);

        assertNotNull(modifiablePrimary);
        assertTrue(modifiablePrimary.validate());
        assertEquals(2, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addMember(new IdentifierNode("a"))
                .addMember(new IdentifierNode("a"));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryIndexer() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a[0]").tryParseModifiablePrimary(0, 4);

        assertNotNull(modifiablePrimary);
        assertTrue(modifiablePrimary.validate());
        assertEquals(1, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addIndexer(integerExpression(0));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryIndexersSequence() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a[0][0]").tryParseModifiablePrimary(0, 7);

        assertNotNull(modifiablePrimary);
        assertTrue(modifiablePrimary.validate());
        assertEquals(2, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addIndexer(integerExpression(0))
                .addIndexer(integerExpression(0));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryCombination() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a[0].a[0].a").tryParseModifiablePrimary(0, 11);

        assertNotNull(modifiablePrimary);
        assertTrue(modifiablePrimary.validate());
        assertEquals(4, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addIndexer(integerExpression(0))
                .addMember(new IdentifierNode("a"))
                .addIndexer(integerExpression(0))
                .addMember(new IdentifierNode("a"));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testGlobalVariableDeclarationType() throws InvalidLexemeException {
        var variable = createParser("var a : integer").tryParseVariableDeclaration(0, 4);

        assertNotNull(variable);
        assertTrue(variable.validate());
        assertEquals(new VariableDeclarationNode(
                new IdentifierNode("a"),
                new PrimitiveTypeNode(PrimitiveType.INTEGER),
                null), variable);
    }

    public void testGlobalVariableDeclarationTypeAndExpression() throws InvalidLexemeException {
        var variable = createParser("var a : integer is 1").tryParseVariableDeclaration(0, 6);

        assertNotNull(variable);
        assertTrue(variable.validate());
        assertEquals(new VariableDeclarationNode(
                new IdentifierNode("a"),
                new PrimitiveTypeNode(PrimitiveType.INTEGER), integerExpression(1)), variable);
    }

    public void testGlobalVariableDeclarationExpression() throws InvalidLexemeException {
        var variable = createParser("var a is 1").tryParseVariableDeclaration(0, 4);

        assertNotNull(variable);
        assertTrue(variable.validate());
        assertEquals(new VariableDeclarationNode(new IdentifierNode("a"), null, integerExpression(1)),
                variable);
    }

    public void testExpressionRealLiteral() throws InvalidLexemeException {
        var expression = createParser("1.0").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(realExpression(1.0), expression);
    }

    public void testExpressionBooleanLiterals() throws InvalidLexemeException {
        var expression = createParser("false").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(booleanExpression(false), expression);

        expression = createParser("true").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(booleanExpression(true), expression);
    }

    public void testComplexSummand() throws InvalidLexemeException {
        var summand = createParser("1*(2-3)").tryParseSummand(0, 7);

        var expectedSummand = new SummandNode(new IntegralLiteralNode(1));

        var innerSimple = toSimple(new IntegralLiteralNode(2))
                .addSummand(AdditionOperator.MINUS, new SummandNode(new IntegralLiteralNode(3)));

        expectedSummand.addFactor(MultiplicationOperator.MULTIPLY, toExpression(innerSimple));

        assertNotNull(summand);
        assertTrue(summand.validate());
        assertEquals(expectedSummand, summand);
    }

    public void testComplexSimple() throws InvalidLexemeException {
        var simple = createParser("1+(2-3)").tryParseSimple(0, 7);

        var expectedSimple = toSimple(new IntegralLiteralNode(1));
        var parentheses = createParser("2-3").tryParseExpression(0, 3);
        expectedSimple.addSummand(AdditionOperator.PLUS, new SummandNode(parentheses));

        assertNotNull(simple);
        assertTrue(simple.validate());
        assertEquals(expectedSimple, simple);
    }

    public void testMoreComplexSimple() throws InvalidLexemeException {
        var simple = createParser("1+2*(3-4)").tryParseSimple(0, 9);

        var expectedSimple = toSimple(new IntegralLiteralNode(1));
        var multiplication = new SummandNode(new IntegralLiteralNode(2));
        var parenthesesSimple = toSimple(new IntegralLiteralNode(3))
                .addSummand(AdditionOperator.MINUS, new SummandNode(new IntegralLiteralNode(4)));

        var parentheses = toExpression(parenthesesSimple);

        multiplication.addFactor(MultiplicationOperator.MULTIPLY, parentheses);
        expectedSimple.addSummand(AdditionOperator.PLUS, multiplication);

        assertNotNull(simple);
        assertTrue(simple.validate());
        assertEquals(expectedSimple, expectedSimple);
    }

    public void testComplexRelation() throws InvalidLexemeException {
        var relation = createParser("5>1+2*(3-4)").tryParseRelation(0, 11);

        var expectedRelation = new BinaryRelationNode(toSimple(new IntegralLiteralNode(5)),
                BinaryRelationNode.Comparison.GREATER,
                createParser("1+2*(3-4)").tryParseSimple(0, 9));

        assertNotNull(relation);
        assertTrue(relation.validate());
        assertEquals(expectedRelation, relation);
    }

    public void testComplexExpression() throws InvalidLexemeException {
        var expression = createParser("1 and 5>1+2*(3-4)").tryParseExpression(0, 13);

        var leftSide = toRelation(new IntegralLiteralNode(1));
        var rightSide = createParser("5>1+2*(3-4)").tryParseRelation(0, 11);
        var expectedExpression = new ExpressionNode(leftSide)
                .addRelation(LogicalOperator.AND, rightSide);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(expectedExpression, expression);
    }

    public void testNestedExpression() throws InvalidLexemeException {
        var expression = createParser("(1>3)").tryParseExpression(0, 5);

        var innerExpression = createParser("1>3").tryParseExpression(0, 3);
        var expectedExpression = toExpression(innerExpression);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(expectedExpression, expression);
    }

    public void testNotExpression() throws InvalidLexemeException {
        var expression = createParser("not false").tryParseExpression(0, 2);

        var innerRelation = createParser("false").tryParseRelation(0, 1);
        var expectedExpression = new ExpressionNode(new NegatedRelationNode(innerRelation));

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(expectedExpression, expression);
    }

    public void testComplexNotExpression() throws InvalidLexemeException {
        var expression = createParser("(not 1) and (not true)").tryParseExpression(0, 9);

        var leftRelation = createParser("(not 1)").tryParseRelation(0, 4);
        var rightRelation = createParser("(not true)").tryParseRelation(0, 4);
        var expectedExpression = new ExpressionNode(leftRelation)
                .addRelation(LogicalOperator.AND, rightRelation);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(expectedExpression, expression);
    }

    public void testSeveralVariableDeclarations() throws InvalidLexemeException {
        var program = parse("\nvar a is 1\n\nvar b is 2\n\n");

        var expectedProgram = new ProgramNode()
                .addDeclaration(createParser("var a is 1").tryParseVariableDeclaration(0, 4))
                .addDeclaration(createParser("var b is 2").tryParseVariableDeclaration(0, 4));

        assertNotNull(program);
        assertTrue(program.validate());
        assertEquals(expectedProgram, program);
    }

    public void testRecord() throws InvalidLexemeException {
        var record = createParser("record \nvar a is 1\n end").tryParseRecordType(0, 8);

        var variable = createParser("var a is 1").tryParseVariableDeclaration(0, 4);
        var expectedRecord = new RecordTypeNode().addVariable(variable);

        assertNotNull(record);
        assertTrue(record.validate());
        assertEquals(expectedRecord, record);
    }

    public void testComplexRecord() throws InvalidLexemeException {
        var record = createParser("record \nvar a is 1; var b is 2\n end").tryParseRecordType(0, 13);

        var variable1 = createParser("var a is 1").tryParseVariableDeclaration(0, 4);
        var variable2 = createParser("var b is 2").tryParseVariableDeclaration(0, 4);
        var expectedRecord = new RecordTypeNode().addVariable(variable1).addVariable(variable2);

        assertNotNull(record);
        assertTrue(record.validate());
        assertEquals(expectedRecord, record);
    }

    public void testEmptyRecord() throws InvalidLexemeException {
        var record = createParser("record end").tryParseRecordType(0, 2);

        assertNotNull(record);
        assertTrue(record.validate());
        assertEquals(new RecordTypeNode(), record);
    }

    public void testNotSeparatedRecord_Invalid() throws InvalidLexemeException {
        var record = createParser("record \nvar a is 1 var b is 2\n end").tryParseRecordType(0, 12);

        assertNull(record);
    }

    public void testArrayType() throws InvalidLexemeException {
        var array = createParser("array [10] integer").tryParseArrayType(0, 5);

        var expectedArray = new ArrayTypeNode(integerExpression(10), new PrimitiveTypeNode(PrimitiveType.INTEGER));
        assertNotNull(array);
        assertTrue(array.validate());
        assertEquals(expectedArray, array);
    }

    public void testIntegerWithMinus() throws InvalidLexemeException {
        var primary = createParser("- 1").tryParsePrimary(0, 2);

        assertNotNull(primary);
        assertTrue(primary.validate());
        assertEquals(IntegralLiteralNode.minus(1), primary);
    }

    public void testIntegerWithPlus() throws InvalidLexemeException {
        var primary = createParser("+ 1").tryParsePrimary(0, 2);

        assertNotNull(primary);
        assertTrue(primary.validate());
        assertEquals(IntegralLiteralNode.plus(1), primary);
    }

    public void testIntegerWithNot() throws InvalidLexemeException {
        var primary = createParser("not 1").tryParsePrimary(0, 2);

        assertNotNull(primary);
        assertTrue(primary.validate());
        assertEquals(IntegralLiteralNode.not(1), primary);
    }

    public void testRealWithMinus() throws InvalidLexemeException {
        var primary = createParser("- 1.0").tryParsePrimary(0, 2);

        assertNotNull(primary);
        assertTrue(primary.validate());
        assertEquals(RealLiteralNode.minus(1.0), primary);
    }

    public void testRealWithPlus() throws InvalidLexemeException {
        var primary = createParser("+ 1.0").tryParsePrimary(0, 2);

        assertNotNull(primary);
        assertTrue(primary.validate());
        assertEquals(RealLiteralNode.plus(1.0), primary);
    }

    public void testArrayOfArrays() throws InvalidLexemeException {
        var array = createParser("array [10] array [10] integer").tryParseArrayType(0, 9);

        var expectedArray = new ArrayTypeNode(integerExpression(10),
                new ArrayTypeNode(integerExpression(10), new PrimitiveTypeNode(PrimitiveType.INTEGER)));
        assertNotNull(array);
        assertTrue(array.validate());
        assertEquals(expectedArray, array);
    }

    public void testPrimitiveTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is integer").tryParseTypeDeclaration(0, 4);

        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"), new PrimitiveTypeNode(PrimitiveType.INTEGER));
        assertNotNull(type);
        assertTrue(type.validate());
        assertEquals(expectedType, type);
    }

    public void testArrayTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is array [10] integer").tryParseTypeDeclaration(0, 8);

        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"),
                new ArrayTypeNode(integerExpression(10), new PrimitiveTypeNode(PrimitiveType.INTEGER)));
        assertNotNull(type);
        assertTrue(type.validate());
        assertEquals(expectedType, type);
    }

    public void testEmptyRecordTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is record end").tryParseTypeDeclaration(0, 5);

        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"), new RecordTypeNode());
        assertNotNull(type);
        assertTrue(type.validate());
        assertEquals(expectedType, type);
    }

    public void testEmptyRecordDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is record var a is 1 end").tryParseTypeDeclaration(0, 9);

        var record = new RecordTypeNode();
        record.variables.add(createParser("var a is 1").tryParseVariableDeclaration(0, 4));
        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"), record);
        assertNotNull(type);
        assertTrue(type.validate());
        assertEquals(expectedType, type);
    }

    public void testEmptyRoutine() throws InvalidLexemeException {
        var routine = createParser("routine main() is end").tryParseRoutineDeclaration(0, 6);

        var expectedRoutine = new RoutineDeclarationNode(new IdentifierNode("main"), new ParametersNode(), new BodyNode());
        assertNotNull(routine);
        assertTrue(routine.validate());
        assertEquals(expectedRoutine, routine);
    }

    public void testEmptyRoutineWithReturnType() throws InvalidLexemeException {
        var routine = createParser("routine main(): integer is end").tryParseRoutineDeclaration(0, 8);

        var expectedRoutine = new RoutineDeclarationNode(new IdentifierNode("main"), new ParametersNode(),
                new PrimitiveTypeNode(PrimitiveType.INTEGER), new BodyNode());
        assertNotNull(routine);
        assertTrue(routine.validate());
        assertEquals(expectedRoutine, routine);
    }

    public void testEmptyRoutineParameters() throws InvalidLexemeException {
        var routine = createParser("routine main(a: integer, b: real): integer is end").tryParseRoutineDeclaration(0, 15);

        var parameters = new ParametersNode()
                .addParameter(new IdentifierNode("a"), new PrimitiveTypeNode(PrimitiveType.INTEGER))
                .addParameter(new IdentifierNode("b"), new PrimitiveTypeNode(PrimitiveType.REAL));

        var expectedRoutine = new RoutineDeclarationNode(new IdentifierNode("main"), parameters,
                new PrimitiveTypeNode(PrimitiveType.INTEGER), new BodyNode());
        assertNotNull(routine);
        assertTrue(routine.validate());
        assertEquals(expectedRoutine, routine);
    }

    public void testEmptyBody() throws InvalidLexemeException {
        var body = createParser("").tryParseBody(0, 0);

        assertNotNull(body);
        assertTrue(body.validate());
        assertEquals(new BodyNode(), body);
    }

    public void testEmptyParameters() throws InvalidLexemeException {
        var parameters = createParser("").tryParseParameters(0, 0);

        assertNotNull(parameters);
        assertTrue(parameters.validate());
        assertEquals(new ParametersNode(), parameters);
    }

    public void testSingleParameter() throws InvalidLexemeException {
        var parameters = createParser("a: integer").tryParseParameters(0, 3);

        var expectedParameters = new ParametersNode()
                .addParameter(new IdentifierNode("a"), new PrimitiveTypeNode(PrimitiveType.INTEGER));

        assertNotNull(parameters);
        assertTrue(parameters.validate());
        assertEquals(expectedParameters, parameters);
    }

    public void testSeveralParameters() throws InvalidLexemeException {
        var parameters = createParser("a: integer, b: real, c: boolean").tryParseParameters(0, 11);

        var expectedParameters = new ParametersNode()
                .addParameter(new IdentifierNode("a"), new PrimitiveTypeNode(PrimitiveType.INTEGER))
                .addParameter(new IdentifierNode("b"), new PrimitiveTypeNode(PrimitiveType.REAL))
                .addParameter(new IdentifierNode("c"), new PrimitiveTypeNode(PrimitiveType.BOOLEAN));
        assertNotNull(parameters);
        assertTrue(parameters.validate());
        assertEquals(expectedParameters, parameters);
    }

    public void testParametersWithEndingComma_Invalid() throws InvalidLexemeException {
        var parameters = createParser("a: integer,").tryParseParameters(0, 4);
        assertNull(parameters);
    }

    public void testBasicAssignment() throws InvalidLexemeException {
        var assignment = createParser("a := 1").tryParseAssignment(0, 3);

        assertNotNull(assignment);
        assertTrue(assignment.validate());
        assertEquals(new AssignmentNode(new ModifiablePrimaryNode(new IdentifierNode("a")), integerExpression(1)), assignment);
    }

    public void testRoutineCallWithOneArgument() throws InvalidLexemeException {
        var call = createParser("main(1)").tryParseRoutineCall(0, 4);

        var expectedCall = new RoutineCallNode(new IdentifierNode("main"));
        expectedCall.arguments.add(integerExpression(1));

        assertNotNull(call);
        assertTrue(call.validate());
        assertEquals(expectedCall, call);
    }

    public void testRoutineCallWithSeveralArguments() throws InvalidLexemeException {
        var call = createParser("main(1, 2, 3)").tryParseRoutineCall(0, 8);

        var expectedCall = new RoutineCallNode(new IdentifierNode("main"))
                .addArgument(integerExpression(1))
                .addArgument(integerExpression(2))
                .addArgument(integerExpression(3));

        assertNotNull(call);
        assertTrue(call.validate());
        assertEquals(expectedCall, call);
    }

    public void testRoutineCallWithEndingComma_Invalid() throws InvalidLexemeException {
        var call = createParser("main(1,)").tryParseRoutineCall(0, 5);

        assertNull(call);
    }

    public void testRoutineCallWithEmptyParentheses() throws InvalidLexemeException {
        var call = createParser("main()").tryParseRoutineCall(0, 3);

        assertNotNull(call);
        assertTrue(call.validate());
    }

    public void testEmptyWhileLoop() throws InvalidLexemeException {
        var loop = createParser("while 1 loop end").tryParseWhileLoop(0, 4);

        assertNotNull(loop);
        assertTrue(loop.validate());
        assertEquals(new WhileLoopNode(integerExpression(1), new BodyNode()), loop);
    }

    public void testWhileLoopWithoutCondition_Invalid() throws InvalidLexemeException {
        var loop = createParser("while loop end").tryParseWhileLoop(0, 3);

        assertNull(loop);
    }

    public void testWhileLoopWithoutLoop_Invalid() throws InvalidLexemeException {
        var loop = createParser("while 1 end").tryParseWhileLoop(0, 3);

        assertNull(loop);
    }

    public void testWhileLoopWithSingleStatement() throws InvalidLexemeException {
        var loop = createParser("while 1 loop i := 1 end").tryParseWhileLoop(0, 7);

        var body = new BodyNode();
        body.statements.add(new AssignmentNode(new ModifiablePrimaryNode(new IdentifierNode("i")), integerExpression(1)));
        var expectedLoop = new WhileLoopNode(integerExpression(1), body);
        assertNotNull(loop);
        assertTrue(loop.validate());
        assertEquals(expectedLoop, loop);
    }

    public void testRange() throws InvalidLexemeException {
        var range = createParser("in 1..1").tryParseRange(0, 4);

        assertNotNull(range);
        assertTrue(range.validate());
        assertEquals(new RangeNode(integerExpression(1), integerExpression(1), false), range);
    }

    public void testRangeReverse() throws InvalidLexemeException {
        var range = createParser("in reverse 1..1").tryParseRange(0, 5);

        assertNotNull(range);
        assertTrue(range.validate());
        assertEquals(new RangeNode(integerExpression(1), integerExpression(1), true), range);
    }

    public void testEmptyForLoop() throws InvalidLexemeException {
        var loop = createParser("for i in 1..1 loop end").tryParseForLoop(0, 8);

        var expectedLoop = new ForLoopNode(new IdentifierNode("i"),
                new RangeNode(integerExpression(1), integerExpression(1), false),
                new BodyNode());
        assertNotNull(loop);
        assertTrue(loop.validate());
        assertEquals(expectedLoop, loop);
    }

    public void testForLoop() throws InvalidLexemeException {
        var loop = createParser("for i in 1..1 loop main(1) end").tryParseForLoop(0, 12);

        var body = new BodyNode();
        body.statements.add(new RoutineCallNode(new IdentifierNode("main")).addArgument(integerExpression(1)));
        var expectedLoop = new ForLoopNode(new IdentifierNode("i"),
                new RangeNode(integerExpression(1), integerExpression(1), false),
                body);
        assertNotNull(loop);
        assertTrue(loop.validate());
        assertEquals(expectedLoop, loop);
    }

    public void testForWithoutVariable_Invalid() throws InvalidLexemeException {
        var loop = createParser("for in 1..1 loop end").tryParseForLoop(0, 7);

        assertNull(loop);
    }

    public void testForWithoutRange_Invalid() throws InvalidLexemeException {
        var loop = createParser("for i in loop end").tryParseForLoop(0, 5);

        assertNull(loop);
    }

    public void testForWithoutLoop_Invalid() throws InvalidLexemeException {
        var loop = createParser("for i in 1..1 end").tryParseForLoop(0, 7);

        assertNull(loop);
    }

    public void testEmptyIfStatement() throws InvalidLexemeException {
        var ifStatement = createParser("if 1 then end").tryParseIfStatement(0, 4);

        assertNotNull(ifStatement);
        assertTrue(ifStatement.validate());
        assertEquals(new IfStatementNode(integerExpression(1), new BodyNode()), ifStatement);
    }

    public void testEmptyIfAndElseStatements() throws InvalidLexemeException {
        var ifStatement = createParser("if 1 then else end").tryParseIfStatement(0, 5);

        assertNotNull(ifStatement);
        assertTrue(ifStatement.validate());
        assertEquals(new IfStatementNode(integerExpression(1), new BodyNode(), new BodyNode()), ifStatement);
    }

    public void testIfStatementWithoutCondition_Invalid() throws InvalidLexemeException {
        var ifStatement = createParser("if then end").tryParseIfStatement(0, 3);

        assertNull(ifStatement);
    }

    public void testIfAndElseStatements() throws InvalidLexemeException {
        var ifStatement = createParser("if 1 then main(1) else main(1) end").tryParseIfStatement(0, 13);

        var body = new BodyNode()
                .add(new RoutineCallNode(new IdentifierNode("main"))
                        .addArgument(integerExpression(1)));
        assertNotNull(ifStatement);
        assertTrue(ifStatement.validate());
        assertEquals(new IfStatementNode(integerExpression(1), body, body), ifStatement);
    }

    public void testComplexRoutine() throws InvalidLexemeException {
        var routine = createParser("routine main() is\nvar a is 1\nvar b is 2\nend")
                .tryParseRoutineDeclaration(0, 17);

        var expectedBody = new BodyNode()
                .add(new VariableDeclarationNode(new IdentifierNode("a"), null, integerExpression(1)))
                .add(new VariableDeclarationNode(new IdentifierNode("b"), null, integerExpression(2)));
        var expectedRoutine = new RoutineDeclarationNode(new IdentifierNode("main"), new ParametersNode(), expectedBody);
        assertNotNull(routine);
        assertTrue(routine.validate());
        assertEquals(expectedRoutine, routine);
    }

    public void testComplexBody() throws InvalidLexemeException {
        var body = createParser("\nvar a is 1\nvar b is 1\n")
                .tryParseBody(0, 11);

        var expectedBody = new BodyNode()
                .add(new VariableDeclarationNode(new IdentifierNode("a"), null, integerExpression(1)))
                .add(new VariableDeclarationNode(new IdentifierNode("b"), null, integerExpression(1)));
        assertNotNull(body);
        assertTrue(body.validate());
        assertEquals(expectedBody, body);
    }

    public void testComplexTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type arr8 is array[8] integer")
                .tryParseTypeDeclaration(0, 8);

        assertNotNull(type);
        assertTrue(type.validate());
        assertEquals(new TypeDeclarationNode(new IdentifierNode("arr8"),
                new ArrayTypeNode(integerExpression(8), new PrimitiveTypeNode(PrimitiveType.INTEGER))),
                type);
    }

    public void testBooleanSizeArray() throws InvalidLexemeException {
        var type = createParser("type booleanArray is array[true or false] boolean")
                .tryParseTypeDeclaration(0, 10);

        assertNotNull(type);
        assertTrue(type.validate());
    }

    public void testArraySize() throws InvalidLexemeException {
        var modifiable = createParser("a.size")
                .tryParseModifiablePrimary(0, 3);

        assertNotNull(modifiable);
        assertTrue(modifiable.validate());
        assertEquals(new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addArraySize(), modifiable);
    }

    public void testManyBrackets1() throws InvalidLexemeException {
        var expression = createParser("((1 + 3) * (5 - 2)) * 3")
                .tryParseExpression(0, 15);

        assertNotNull(expression);
        assertTrue(expression.validate());
    }

    public void testManyBrackets2() throws InvalidLexemeException {
        var expression = createParser("1.0 * (((4.2 / 2.1) + 5.0) - 2.3)")
                .tryParseExpression(0, 15);

        assertNotNull(expression);
        assertTrue(expression.validate());
    }

    public void testReturnStatement() throws InvalidLexemeException {
        var returnStatement = createParser("return 0").tryParseReturn(0, 2);

        assertNotNull(returnStatement);
        assertTrue(returnStatement.validate());
        assertEquals(new ReturnStatementNode(integerExpression(0)), returnStatement);
    }

    public void testBodyWithReturn() throws InvalidLexemeException {
        var body = createParser("return 0").tryParseBody(0, 2);

        assertNotNull(body);
        assertTrue(body.validate());
        assertEquals(new BodyNode().add(new ReturnStatementNode(integerExpression(0))), body);
    }

    public void testEmptyReturn() throws InvalidLexemeException {
        var returnStatement = createParser("return").tryParseReturn(0, 1);

        assertNotNull(returnStatement);
        assertTrue(returnStatement.validate());
        assertEquals(new ReturnStatementNode(), returnStatement);
    }

    public void testRoutineWithReturn() throws InvalidLexemeException {
        var routine = createParser("routine main(): integer is return 0 end")
                .tryParseRoutineDeclaration(0, 10);

        var body = new BodyNode()
                .add(new ReturnStatementNode(integerExpression(0)));

        var expectedRoutine = new RoutineDeclarationNode(new IdentifierNode("main"),
                new ParametersNode(),
                new PrimitiveTypeNode(PrimitiveType.INTEGER),
                body);

        assertNotNull(routine);
        assertTrue(routine.validate());
        assertEquals(expectedRoutine, routine);
    }

    public void testBigBody1() throws InvalidLexemeException {
        var parser = createParser("var a: integer\n" +
                "\n" +
                "    a := 1\n" +
                "    a := 1; a := 2\n" +
                "    a := 1; a := 2; a := 3\n" +
                "    a := 1; a := 2; a := 3; a := 4\n" +
                "    a := 1; a := 2; a := 3; a := 4; a := 5\n" +
                "    a := 1; a := 2; a := 3; a := 4\n" +
                "    a := 1; a := 2; a := 3\n" +
                "    a := 1; a := 2\n" +
                "    a := 1");

        var body = parser.tryParseBody(0, parser.getTokensCount());

        assertNotNull(body);
        assertTrue(body.validate());
    }

    public void testBigBody2() throws InvalidLexemeException {
        var parser = createParser("var result is 0\n" +
                "\n" +
                "    for i in 1 .. arr.size loop\n" +
                "        result := result + arr[i]\n" +
                "    end\n" +
                "\n" +
                "    return result");

        var body = parser.tryParseBody(0, parser.getTokensCount());

        assertNotNull(body);
        assertTrue(body.validate());
    }

    public void testBigBody3() throws InvalidLexemeException {
        var parser = createParser("var sum is 0\n" +
                "\n" +
                "    for i in 1 .. 10 loop\n" +
                "        sum := sum + i\n" +
                "    end\n" +
                "\n" +
                "    for i in reverse 1 .. a.size loop\n" +
                "        sum := sum + i\n" +
                "    end");

        var body = parser.tryParseBody(0, parser.getTokensCount());

        assertNotNull(body);
        assertTrue(body.validate());
    }

    public void testBigRoutine1() throws InvalidLexemeException {
        var parser = createParser("routine get_sum(arr: array[] integer): integer is\n" +
                "    var result is 0\n" +
                "\n" +
                "    for i in 1 .. arr.size loop\n" +
                "        result := result + arr[i]\n" +
                "    end\n" +
                "\n" +
                "    return result\n" +
                "end");

        var routine = parser.tryParseRoutineDeclaration(0, parser.getTokensCount());

        assertNotNull(routine);
        assertTrue(routine.validate());
    }

    public void testBigRoutine2() throws InvalidLexemeException {
        var parser = createParser("routine main() is\n" +
                "    var sum is 0\n" +
                "\n" +
                "    for i in 1 .. 10 loop\n" +
                "        sum := sum + i\n" +
                "    end\n" +
                "\n" +
                "    for i in reverse 1 .. a.size loop\n" +
                "        sum := sum + i\n" +
                "    end\n" +
                "end");

        var routine = parser.tryParseRoutineDeclaration(0, parser.getTokensCount());

        assertNotNull(routine);
        assertTrue(routine.validate());
    }

    public void testComplexLoop() throws InvalidLexemeException {
        var parser = createParser("for i in 1 .. arr.size loop\n" +
                "result := result + arr[i]\n" +
                "end");

        var body = parser.tryParseForLoop(0, parser.getTokensCount());

        assertNotNull(body);
        assertTrue(body.validate());
    }

    public void testArrayWithoutSize() throws InvalidLexemeException {
        var array = createParser("array[] integer").tryParseArrayType(0, 4);

        assertNotNull(array);
        assertTrue(array.validate());
        assertEquals(new ArrayTypeNode(null, new PrimitiveTypeNode(PrimitiveType.INTEGER)), array);
    }

    public void testReverseForLoop() throws InvalidLexemeException {
        var parser = createParser("for i in reverse 1 .. a.size loop\n" +
                "        sum := sum + i\n" +
                "    end");

        var loop = parser.tryParseForLoop(0, parser.getTokensCount());

        assertNotNull(loop);
        assertTrue(loop.validate());
        assertTrue(loop.validate());
    }

    public void testArrayVariableDeclaration() throws InvalidLexemeException {
        var parser = createParser("var a: array[10] integer");

        var declaration = parser.tryParseVariableDeclaration(0, parser.getTokensCount());

        assertNotNull(declaration);
        assertTrue(declaration.validate());
        assertTrue(declaration.validate());
    }

    public void testProgramWithError_Invalid() throws InvalidLexemeException {
        var parser = createParser("var a is 2\n\n" +
                "routine main()\n" +
                "    var a: integer\n" +
                "    a := 1\n" +
                "    var b is 2\n" +
                "    var c is a + b\n" +
                "end");

        var program = parser.tryParseProgram();

        assertNull(program);
    }

    public void testSimpleNoParentheses() throws InvalidLexemeException {
        var simple = createParser("1*3+2").tryParseSimple(0, 5);

        var product = new SummandNode(new IntegralLiteralNode(1))
                .addFactor(MultiplicationOperator.MULTIPLY, new IntegralLiteralNode(3));
        var expectedSimple = new SimpleNode(product)
                .addSummand(AdditionOperator.PLUS, new SummandNode(new IntegralLiteralNode(2)));

        assertNotNull(simple);
        assertTrue(simple.validate());
        assertEquals(expectedSimple, simple);
    }

    public void testSimpleNoParenthesesInverseOrder() throws InvalidLexemeException {
        var simple = createParser("2+1*3").tryParseSimple(0, 5);

        var product = new SummandNode(new IntegralLiteralNode(1))
            .addFactor(MultiplicationOperator.MULTIPLY, new IntegralLiteralNode(3));
        var expectedSimple = toSimple(new IntegralLiteralNode(2))
                .addSummand(AdditionOperator.PLUS, product);

        assertNotNull(simple);
        assertTrue(simple.validate());
        assertEquals(expectedSimple, simple);
    }

    public void testCompleteExpression() throws InvalidLexemeException {
        var expression = createParser("true or not 1 < 1*(3-2)").tryParseExpression(0, 12);

        var expectedExpression = toExpression(trueLiteral);

        var parenthesesSimple = toSimple(new IntegralLiteralNode(3))
                .addSummand(AdditionOperator.MINUS, new SummandNode(new IntegralLiteralNode(2)));
        var parenthesisExpression = toExpression(parenthesesSimple);

        var product = new SummandNode(new IntegralLiteralNode(1))
            .addFactor(MultiplicationOperator.MULTIPLY, parenthesisExpression);

        var rightRelation = new BinaryRelationNode(toSimple(new IntegralLiteralNode(1)),
                BinaryRelationNode.Comparison.LESS,
                new SimpleNode(product));
        var negatedRightRelation = new NegatedRelationNode(rightRelation);

        expectedExpression.addRelation(LogicalOperator.OR, negatedRightRelation);

        assertNotNull(expression);
        assertTrue(expression.validate());
        assertEquals(expectedExpression, expression);
    }

    public void testCompleteRelation() throws InvalidLexemeException {
        var relation = createParser("not 1 < 1*(3-2)").tryParseRelation(0, 10);

        var parenthesesSimple = toSimple(new IntegralLiteralNode(3));
        parenthesesSimple.addSummand(AdditionOperator.MINUS, new SummandNode(new IntegralLiteralNode(2)));
        var parenthesisExpression = toExpression(parenthesesSimple);

        var product = new SummandNode(new IntegralLiteralNode(1));
        product.addFactor(MultiplicationOperator.MULTIPLY, parenthesisExpression);

        var rightRelation = new BinaryRelationNode(toSimple(new IntegralLiteralNode(1)),
                BinaryRelationNode.Comparison.LESS,
                new SimpleNode(product));
        var expectedRelation = new NegatedRelationNode(rightRelation);

        assertNotNull(relation);
        assertTrue(relation.validate());
        assertEquals(expectedRelation, relation);
    }

    public void testBoolean() throws InvalidLexemeException {
        var booleanLiteral = createParser("true").tryParseBooleanLiteral(0, 1);

        assertNotNull(booleanLiteral);
        assertTrue(booleanLiteral.validate());
        assertEquals(trueLiteral, booleanLiteral);
    }

    public void testNestedRecord() throws InvalidLexemeException {
        var variable = createParser("var a : record var b : record var a is 1 end end").tryParseVariableDeclaration(0, 14);

        assertNotNull(variable);
    }

    public void testReverseRange() throws InvalidLexemeException {
        var range = createParser("in reverse 1..5").tryParseRange(0, 5);

        assertNotNull(range);
    }

    public void testFuncLoop() throws InvalidLexemeException {
        var loop = createParser("while not a(b(1)) loop end").tryParseWhileLoop(0, 11);
        assertNotNull(loop);
    }
 }
