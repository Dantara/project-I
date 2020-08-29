package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.javatuples.Pair;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;

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
        return new Parser(new Lexer().scan(programText));
    }

    public void testEmptyProgram() throws InvalidLexemeException {
        var program = parse("");

        assertNotNull(program);
        assertEquals(0, program.Declarations.size());
    }

    public void testIdentifier() throws InvalidLexemeException {
        var identifier = createParser("name").tryParseIdentifier(0, 1);

        assertNotNull(identifier);
        assertEquals(new IdentifierNode("name"), identifier);
    }

    public void testPrimitiveType() throws InvalidLexemeException {
        var primitiveType = createParser("integer").tryParsePrimitiveType(0, 1);

        assertNotNull(primitiveType);
        assertEquals(new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER), primitiveType);
    }

    public void testType() throws InvalidLexemeException {
        var primitiveType = createParser("integer").tryParseType(0, 1);

        assertNotNull(primitiveType);
        assertEquals(new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER), primitiveType);
    }

    public void testExpressionLiteral() throws InvalidLexemeException {
        var expression = createParser("1").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertEquals(ExpressionNode.integerLiteral(1), expression);
    }

    public void testIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseIntegralLiteral(0, 1);

        assertNotNull(literal);
        assertEquals(new IntegralLiteralNode(1), literal);
    }

    public void testPrimaryIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParsePrimary(0, 1);

        assertNotNull(literal);
        assertEquals(new IntegralLiteralNode(1), literal);
    }

    public void testSummandIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseSummand(0, 1);

        assertNotNull(literal);
        assertEquals(new IntegralLiteralNode(1), literal);
    }

    public void testFactorIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseFactor(0, 1);

        assertNotNull(literal);
        assertEquals(new FactorNode(new IntegralLiteralNode(1)), literal);
    }

    public void testSimpleIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseSimple(0, 1);

        assertNotNull(literal);
        assertEquals(new SimpleNode(new FactorNode(new IntegralLiteralNode(1))), literal);
    }

    public void testRelationIntegralLiteral() throws InvalidLexemeException {
        var literal = createParser("1").tryParseRelation(0, 1);

        assertNotNull(literal);
        assertEquals(new BinaryRelationNode(new SimpleNode(new FactorNode(new IntegralLiteralNode(1)))), literal);
    }

    public void testModifiablePrimaryMember() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a.a").tryParseModifiablePrimary(0, 3);

        assertNotNull(modifiablePrimary);
        assertEquals(1, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addMember(new IdentifierNode("a"));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryMembersSequence() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a.a.a").tryParseModifiablePrimary(0, 5);

        assertNotNull(modifiablePrimary);
        assertEquals(2, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addMember(new IdentifierNode("a"))
                .addMember(new IdentifierNode("a"));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryIndexer() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a[0]").tryParseModifiablePrimary(0, 4);

        assertNotNull(modifiablePrimary);
        assertEquals(1, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addIndexer(ExpressionNode.integerLiteral(0));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryIndexersSequence() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a[0][0]").tryParseModifiablePrimary(0, 7);

        assertNotNull(modifiablePrimary);
        assertEquals(2, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addIndexer(ExpressionNode.integerLiteral(0))
                .addIndexer(ExpressionNode.integerLiteral(0));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testModifiablePrimaryCombination() throws InvalidLexemeException {
        var modifiablePrimary  = createParser("a[0].a[0].a").tryParseModifiablePrimary(0, 11);

        assertNotNull(modifiablePrimary);
        assertEquals(4, modifiablePrimary.accessors.size());

        var expectedNode = new ModifiablePrimaryNode(new IdentifierNode("a"))
                .addIndexer(ExpressionNode.integerLiteral(0))
                .addMember(new IdentifierNode("a"))
                .addIndexer(ExpressionNode.integerLiteral(0))
                .addMember(new IdentifierNode("a"));

        assertEquals(expectedNode, modifiablePrimary);
    }

    public void testGlobalVariableDeclarationType() throws InvalidLexemeException {
        var variable = createParser("var a : integer").tryParseVariableDeclaration(0, 4);

        assertNotNull(variable);
        assertEquals(new VariableDeclarationNode(
                new IdentifierNode("a"),
                new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER),
                null), variable);
    }

    public void testGlobalVariableDeclarationTypeAndExpression() throws InvalidLexemeException {
        var variable = createParser("var a : integer is 1").tryParseVariableDeclaration(0, 6);

        assertNotNull(variable);
        assertEquals(new VariableDeclarationNode(
                new IdentifierNode("a"),
                new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER),
                ExpressionNode.integerLiteral(1)), variable);
    }

    public void testGlobalVariableDeclarationExpression() throws InvalidLexemeException {
        var variable = createParser("var a is 1").tryParseVariableDeclaration(0, 4);

        assertNotNull(variable);
        assertEquals(new VariableDeclarationNode(
                new IdentifierNode("a"),
                null,
                ExpressionNode.integerLiteral(1)), variable);
    }

    public void testExpressionRealLiteral() throws InvalidLexemeException {
        var expression = createParser("1.0").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertEquals(ExpressionNode.realLiteral(1.0), expression);
    }

    public void testExpressionBooleanLiterals() throws InvalidLexemeException {
        var expression = createParser("false").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertEquals(ExpressionNode.booleanLiteral(false), expression);

        expression = createParser("true").tryParseExpression(0, 1);

        assertNotNull(expression);
        assertEquals(ExpressionNode.booleanLiteral(true), expression);
    }

    public void testComplexFactor() throws InvalidLexemeException {
        var factor = createParser("1+(2-3)").tryParseFactor(0, 7);

        var expectedFactor = new FactorNode(new IntegralLiteralNode(1));
        var parentheses = createParser("2-3").tryParseExpression(0, 3);
        expectedFactor.otherSummands.add(new Pair<>(FactorNode.Operator.PLUS, parentheses));

        assertNotNull(factor);
        assertEquals(expectedFactor, factor);
    }

    public void testComplexSimple() throws InvalidLexemeException {
        var simple = createParser("1+2*(3-4)").tryParseSimple(0, 9);

        var factor1 = new FactorNode(new IntegralLiteralNode(2));
        var factor2 = new FactorNode(createParser("3-4").tryParseExpression(0, 3));
        var expectedSimple = new SimpleNode(factor1);
        expectedSimple.otherFactors.add(new Pair<>(SimpleNode.Operator.MULTIPLICATION, factor2));

        assertNotNull(simple);
        assertEquals(expectedSimple, expectedSimple);
    }

    public void testComplexRelation() throws InvalidLexemeException {
        var relation = createParser("5>1+2*(3-4)").tryParseRelation(0, 11);

        var expectedRelation = new BinaryRelationNode(new SimpleNode(new FactorNode(new IntegralLiteralNode(5))),
                BinaryRelationNode.Comparison.GREATER,
                createParser("1+2*(3-4)").tryParseSimple(0, 9));
        assertNotNull(relation);
        assertEquals(expectedRelation, relation);
    }

    public void testComplexExpression() throws InvalidLexemeException {
        var expression = createParser("1 and 5>1+2*(3-4)").tryParseExpression(0, 13);

        var leftSide = new BinaryRelationNode(new SimpleNode(new FactorNode(new IntegralLiteralNode(1))));
        var rightSide = createParser("5>1+2*(3-4)").tryParseRelation(0, 11);
        var expectedExpression = new ExpressionNode(leftSide);
        expectedExpression.otherRelations.add(new Pair<>(ExpressionNode.Operator.AND, rightSide));

        assertNotNull(expression);
        assertEquals(expectedExpression, expression);
    }

    public void testNestedExpression() throws InvalidLexemeException {
        var expression = createParser("(1>3)").tryParseExpression(0, 5);

        var innerExpression = createParser("1>3").tryParseExpression(0, 3);
        var expectedExpression = new ExpressionNode(new BinaryRelationNode(new SimpleNode(new FactorNode(innerExpression))));

        assertNotNull(expression);
        assertEquals(expectedExpression, expression);
    }

    public void testNotExpression() throws InvalidLexemeException {
        var expression = createParser("not false").tryParseExpression(0, 2);

        var innerRelation = createParser("false").tryParseRelation(0, 1);
        var expectedExpression = new ExpressionNode(new NegatedRelationNode(innerRelation));

        assertNotNull(expression);
        assertEquals(expectedExpression, expression);
    }

    public void testComplexNotExpression() throws InvalidLexemeException {
        var expression = createParser("(not 1) and (not true)").tryParseExpression(0, 9);

        var leftRelation = createParser("(not 1)").tryParseRelation(0, 4);
        var rightRelation = createParser("(not true)").tryParseRelation(0, 4);
        var expectedExpression = new ExpressionNode(leftRelation);
        expectedExpression.otherRelations.add(new Pair<>(ExpressionNode.Operator.AND, rightRelation));

        assertNotNull(expression);
        assertEquals(expectedExpression, expression);
    }

    public void testSeveralVariableDeclarations() throws InvalidLexemeException {
        var program = parse("\nvar a is 1\n\nvar b is 2\n\n");

        var expectedProgram = new ProgramNode();
        expectedProgram.Declarations.add(createParser("var a is 1").tryParseVariableDeclaration(0, 4));
        expectedProgram.Declarations.add(createParser("var b is 2").tryParseVariableDeclaration(0, 4));

        assertNotNull(program);
        assertEquals(expectedProgram, program);
    }

    public void testRecord() throws InvalidLexemeException {
        var record = createParser("record \nvar a is 1\n end").tryParseRecordType(0, 8);

        var variable = createParser("var a is 1").tryParseVariableDeclaration(0, 4);
        var expectedRecord = new RecordTypeNode();
        expectedRecord.Variables.add(variable);

        assertNotNull(record);
        assertEquals(expectedRecord, record);
    }

    public void testComplexRecord() throws InvalidLexemeException {
        var record = createParser("record \nvar a is 1; var b is 2\n end").tryParseRecordType(0, 13);

        var variable1 = createParser("var a is 1").tryParseVariableDeclaration(0, 4);
        var variable2 = createParser("var b is 2").tryParseVariableDeclaration(0, 4);
        var expectedRecord = new RecordTypeNode();
        expectedRecord.Variables.add(variable1);
        expectedRecord.Variables.add(variable2);

        assertNotNull(record);
        assertEquals(expectedRecord, record);
    }

    public void testEmptyRecord() throws InvalidLexemeException {
        var record = createParser("record end").tryParseRecordType(0, 2);

        assertNotNull(record);
        assertEquals(new RecordTypeNode(), record);
    }

    public void testNotSeparatedRecord() throws InvalidLexemeException {
        var record = createParser("record \nvar a is 1 var b is 2\n end").tryParseRecordType(0, 12);

        assertNull(record);
    }

    public void testArrayType() throws InvalidLexemeException {
        var array = createParser("array [10] integer").tryParseArrayType(0, 5);

        var expectedArray = new ArrayTypeNode(ExpressionNode.integerLiteral(10), new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER));
        assertNotNull(array);
        assertEquals(expectedArray, array);
    }

    public void testArrayOfArrays() throws InvalidLexemeException {
        var array = createParser("array [10] array [10] integer").tryParseArrayType(0, 9);

        var expectedArray = new ArrayTypeNode(ExpressionNode.integerLiteral(10),
                new ArrayTypeNode(ExpressionNode.integerLiteral(10), new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER)));
        assertNotNull(array);
        assertEquals(expectedArray, array);
    }

    public void testPrimitiveTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is integer").tryParseTypeDeclaration(0, 4);

        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"), new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER));
        assertNotNull(type);
        assertEquals(expectedType, type);
    }

    public void testArrayTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is array [10] integer").tryParseTypeDeclaration(0, 8);

        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"),
                new ArrayTypeNode(ExpressionNode.integerLiteral(10), new PrimitiveTypeNode(PrimitiveTypeNode.Type.INTEGER)));
        assertNotNull(type);
        assertEquals(expectedType, type);
    }

    public void testEmptyRecordTypeDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is record end").tryParseTypeDeclaration(0, 5);

        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"), new RecordTypeNode());
        assertNotNull(type);
        assertEquals(expectedType, type);
    }

    public void testEmptyRecordDeclaration() throws InvalidLexemeException {
        var type = createParser("type i is record var a is 1 end").tryParseTypeDeclaration(0, 9);

        var record = new RecordTypeNode();
        record.Variables.add(createParser("var a is 1").tryParseVariableDeclaration(0, 4));
        var expectedType = new TypeDeclarationNode(new IdentifierNode("i"), record);
        assertNotNull(type);
        assertEquals(expectedType, type);
    }
 }
