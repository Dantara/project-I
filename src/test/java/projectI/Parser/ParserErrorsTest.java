package projectI.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import projectI.CodePosition;
import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;
import projectI.Lexer.Token;
import projectI.Lexer.TokenType;
import projectI.Parser.Errors.*;

import static org.junit.Assert.assertArrayEquals;

public class ParserErrorsTest extends TestCase {
    public ParserErrorsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ParserErrorsTest.class);
    }

    private static Parser createParser(String code) throws InvalidLexemeException {
        var lexer = new Lexer();
        var tokens = lexer.scan(code);
        return new Parser(tokens, lexer.getLexemesWithLocations());
    }

    public void testVariableDeclaration_ExpectedIdentifier() throws InvalidLexemeException {
        var parser = createParser("var integer is 1");
        var variable = parser.tryParseVariableDeclaration(0, 4);

        var expectedError = new ExpectedIdentifierError(keyword("integer"), new CodePosition(0, 4));

        assertNull(variable);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testForLoop_ExpectedIdentifier() throws InvalidLexemeException {
        var parser = createParser("for integer in 1..2 loop end");
        var loop = parser.tryParseForLoop(0, 8);

        var expectedError = new ExpectedIdentifierError(keyword("integer"), new CodePosition(0, 4));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testForLoop_ExpectedLoopKeyword() throws InvalidLexemeException {
        var parser = createParser("for a in 1..2 end");
        var loop = parser.tryParseForLoop(0, 7);

        var expectedError = new ExpectedKeywordError("loop", new CodePosition(0, 14));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testForLoop_ExpectedEndKeyword() throws InvalidLexemeException {
        var parser = createParser("for a in 1..2 loop");
        var loop = parser.tryParseForLoop(0, 7);

        var expectedError = new ExpectedKeywordError("end", new CodePosition(0, 19));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testForLoop_ExpectedRange_InKeyword() throws InvalidLexemeException {
        var parser = createParser("for a loop end");
        var loop = parser.tryParseForLoop(0, 4);

        var expectedErrors = new ParsingError[] {
                new ExpectedKeywordError("in", new CodePosition(0, 6)),
                new ExpectedRangeError(new Token[] { }, new CodePosition(0, 6), new CodePosition(0, 6))
        };

        assertNull(loop);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testForLoop_ExpectedRange_RangeOperator() throws InvalidLexemeException {
        var parser = createParser("for a in 1 loop end");
        var loop = parser.tryParseForLoop(0, 6);

        var expectedErrors = new ParsingError[] {
                new ExpectedOperatorError("..", new CodePosition(0, 10)),
                new ExpectedRangeError(new Token[] { keyword("in"), literal("1") }, new CodePosition(0, 6), new CodePosition(0, 10))
        };

        assertNull(loop);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testForLoop_ExpectedRange_FirstExpression() throws InvalidLexemeException {
        var parser = createParser("for a in integer..2 loop end");
        var loop = parser.tryParseForLoop(0, 8);

        var expectedRangeTokens = new Token[] { keyword("in"), keyword("integer"), operator(".."), literal("2") };

        var expectedErrors = new ParsingError[] {
                new ExpectedExpressionError(new Token[] { keyword("integer") }, new CodePosition(0, 9)),
                new ExpectedRangeError(expectedRangeTokens, new CodePosition(0, 6), new CodePosition(0, 19))
        };

        assertNull(loop);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testForLoop_ExpectedRange_SecondExpression() throws InvalidLexemeException {
        var parser = createParser("for a in 2..integer loop end");
        var loop = parser.tryParseForLoop(0, 8);

        var expectedRangeTokens = new Token[] { keyword("in"), literal("2"), operator(".."), keyword("integer") };

        var expectedErrors = new ParsingError[] {
                new ExpectedExpressionError(new Token[] { keyword("integer") }, new CodePosition(0, 12)),
                new ExpectedRangeError(expectedRangeTokens, new CodePosition(0, 6), new CodePosition(0, 19))
        };

        assertNull(loop);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testWhileLoop_ExpectedExpression() throws InvalidLexemeException {
        var parser = createParser("while integer loop end");
        var loop = parser.tryParseWhileLoop(0, 4);

        var expectedError = new ExpectedExpressionError(new Token[] { new Token(TokenType.Keyword, "integer")}, new CodePosition(0, 6));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testWhileLoop_ExpectedLoopKeyword() throws InvalidLexemeException {
        var parser = createParser("while 1 end");
        var loop = parser.tryParseWhileLoop(0, 3);

        var expectedError = new ExpectedKeywordError("loop", new CodePosition(0, 8));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testWhileLoop_ExpectedEndKeyword() throws InvalidLexemeException {
        var parser = createParser("while 1 loop");
        var loop = parser.tryParseWhileLoop(0, 3);

        var expectedError = new ExpectedKeywordError("end", new CodePosition(0, 13));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testTypeDeclaration_ExpectedIdentifier() throws InvalidLexemeException {
        var parser = createParser("type integer is real");
        var loop = parser.tryParseTypeDeclaration(0, 4);

        var expectedError = new ExpectedIdentifierError(keyword("integer"), new CodePosition(0, 5));

        assertNull(loop);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedIdentifier() throws InvalidLexemeException {
        var parser = createParser("routine loop() is end");
        var declaration = parser.tryParseRoutineDeclaration(0, 6);

        var expectedError = new ExpectedIdentifierError(keyword("loop"), new CodePosition(0, 8));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedType() throws InvalidLexemeException {
        var parser = createParser("routine main(): array is end");
        var declaration = parser.tryParseRoutineDeclaration(0, 8);

        var expectedTokens = new Token[] { keyword("array") };
        var expectedError = new ExpectedTypeError(expectedTokens, new CodePosition(0, 16));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedIsKeyword() throws InvalidLexemeException {
        var parser = createParser("routine main() end");
        var declaration = parser.tryParseRoutineDeclaration(0, 5);

        var expectedError = new ExpectedKeywordError("is", new CodePosition(0, 15));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedEndKeyword() throws InvalidLexemeException {
        var parser = createParser("routine main() is");
        var declaration = parser.tryParseRoutineDeclaration(0, 5);

        var expectedError = new ExpectedKeywordError("end", new CodePosition(0, 18));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedParenthesis() throws InvalidLexemeException {
        var parser = createParser("routine main is end");
        var declaration = parser.tryParseRoutineDeclaration(0, 4);

        var expectedError = new ExpectedOperatorError("(", new CodePosition(0, 12));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedClosingParenthesis() throws InvalidLexemeException {
        var parser = createParser("routine main( is end");
        var declaration = parser.tryParseRoutineDeclaration(0, 5);

        var expectedError = new ExpectedOperatorError(")", new CodePosition(0, 13));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineDeclaration_ExpectedClosingParenthesisWithParameters() throws InvalidLexemeException {
        var parser = createParser("routine main(a: integer is end");
        var declaration = parser.tryParseRoutineDeclaration(0, 8);

        var expectedError = new ExpectedOperatorError(")", new CodePosition(0, 13));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testParameterDeclaration_ExpectedIdentifier() throws InvalidLexemeException {
        var parser = createParser("array : array[] integer");
        var declaration = parser.tryParseParameters(0, 6);

        var expectedError = new ExpectedIdentifierError(keyword("array"), new CodePosition(0, 0));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testParameterDeclaration_ExpectedType() throws InvalidLexemeException {
        var parser = createParser("int : var");
        var declaration = parser.tryParseParameters(0, 3);

        var expectedTokens = new Token[] { keyword("var") };
        var expectedError = new ExpectedTypeError(expectedTokens, new CodePosition(0, 6));

        assertNull(declaration);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testArrayType_ExpectedExpression() throws InvalidLexemeException {
        var parser = createParser("array [integer] integer");
        var array = parser.tryParseArrayType(0, 5);

        var expectedTokens = new Token[] { keyword("integer") };
        var expectedError = new ExpectedExpressionError(expectedTokens, new CodePosition(0, 7));

        assertNull(array);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testArrayType_ExpectedType() throws InvalidLexemeException {
        var parser = createParser("array [] loop");
        var array = parser.tryParseArrayType(0, 4);

        var expectedTokens = new Token[] { keyword("loop") };
        var expectedError = new ExpectedTypeError(expectedTokens, new CodePosition(0, 9));

        assertNull(array);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testArrayType_ExpectedBracket() throws InvalidLexemeException {
        var parser = createParser("array integer");
        var array = parser.tryParseArrayType(0, 2);

        var expectedError = new ExpectedOperatorError("[", new CodePosition(0, 5));

        assertNull(array);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testArrayType_ExpectedClosingBracket() throws InvalidLexemeException {
        var parser = createParser("array [ integer");
        var array = parser.tryParseArrayType(0, 3);

        var expectedError = new ExpectedOperatorError("]", new CodePosition(0, 7));

        assertNull(array);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testArrayType_ExpressionCanBeOmitted() throws InvalidLexemeException {
        var parser = createParser("array [] integer");
        var array = parser.tryParseArrayType(0, 4);

        assertNotNull(array);
        assertEquals(0, parser.getErrorCount());
    }

    public void testAssignment_ExpectedModifiablePrimary() throws InvalidLexemeException {
        var parser = createParser("1 := 2");
        var array = parser.tryParseAssignment(0, 3);

        var expectedTokens = new Token[] { literal("1") };
        var expectedError = new ExpectedModifiablePrimaryError(expectedTokens, new CodePosition(0, 0));

        assertNull(array);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testAssignment_ExpectedExpression() throws InvalidLexemeException {
        var parser = createParser("a := record");
        var array = parser.tryParseAssignment(0, 3);

        var expectedTokens = new Token[] { keyword("record") };
        var expectedError = new ExpectedExpressionError(expectedTokens, new CodePosition(0, 5));

        assertNull(array);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testBody_NoErrors() throws InvalidLexemeException {
        var parser = createParser("a := (1)\n b := (2)");
        var body = parser.tryParseBody(0, 11);

        assertNotNull(body);
        assertEquals(0, parser.getErrorCount());
    }

    public void testBody_ExpectedStatement() throws InvalidLexemeException {
        var parser = createParser("a := 1 a := 2");
        var array = parser.tryParseBody(0, 6);

        var expressionTokens = new Token[] {literal("1"), identifier("a"), operator(":="), literal("2")};

        var expectedErrors = new ParsingError[] {
                new ExpectedExpressionError(expressionTokens, new CodePosition(0, 5)),
                new ExpectedStatementError(new CodePosition(0, 0), new CodePosition(0, 13))
        };

        assertNull(array);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testProgram_InvalidVariableDeclaration() throws InvalidLexemeException {
        var parser = createParser("var integer is 2");
        var program = parser.tryParseProgram();

        var expectedErrors = new ParsingError[] {
                new ExpectedIdentifierError(new Token(TokenType.Keyword, "integer"), new CodePosition(0, 4))
        };

        assertNull(program);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testProgram_InvalidRoutineDeclaration_ExpectedIs() throws InvalidLexemeException {
        var parser = createParser("routine main() a := 1 end");
        var program = parser.tryParseProgram();

        var expectedErrors = new ParsingError[] {
                new ExpectedKeywordError("is", new CodePosition(0, 15)),
        };

        assertNull(program);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testProgram_InvalidRoutineDeclaration_ExpectedIdentifier() throws InvalidLexemeException {
        var parser = createParser("routine integer() is a := 1 end");
        var program = parser.tryParseProgram();

        var expectedErrors = new ParsingError[] {
                new ExpectedIdentifierError(new Token(TokenType.Keyword, "integer"), new CodePosition(0, 8))
        };

        assertNull(program);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testIf_ExpectedThenKeyword() throws InvalidLexemeException {
        var parser = createParser("if 1 end");
        var program = parser.tryParseIfStatement(0, 3);

        var expectedError = new ExpectedKeywordError("then", new CodePosition(0, 5));

        assertNull(program);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testIf_ExpectedEndKeyword() throws InvalidLexemeException {
        var parser = createParser("if 1 then");
        var program = parser.tryParseIfStatement(0, 3);

        var expectedError = new ExpectedKeywordError("end", new CodePosition(0, 10));

        assertNull(program);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRecordType_ExpectedEndKeyword() throws InvalidLexemeException {
        var parser = createParser("record");
        var record = parser.tryParseRecordType(0, 1);

        var expectedError = new ExpectedKeywordError("end", new CodePosition(0, 7));

        assertNull(record);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRecordType_ExpectedVariableDeclaration() throws InvalidLexemeException {
        var parser = createParser("record a := 1 end");
        var record = parser.tryParseRecordType(0, 5);

        var expectedTokens = new Token[] { identifier("a"), operator(":="), literal("1") };
        var expectedError = new ExpectedVariableDeclarationError(expectedTokens, new
                CodePosition(0, 7),
                new CodePosition(0, 13));

        assertNull(record);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testProgram_LastStatementsIsInvalid() throws InvalidLexemeException {
        var parser = createParser("routine main() is a := 1; 2 := 2 end");
        var program = parser.tryParseProgram();

        var expectedTokens = new Token[] { literal("2") };
        var expectedErrors = new ParsingError[] {
                new ExpectedModifiablePrimaryError(expectedTokens, new CodePosition(0, 26)),
                new ExpectedStatementError(new CodePosition(0, 26), new CodePosition(0, 32))
        };

        assertNull(program);
        assertArrayEquals(expectedErrors, parser.getErrors().toArray());
    }

    public void testRoutineCall_ExpectedExpression() throws InvalidLexemeException {
        var parser = createParser("main(integer)");
        var call = parser.tryParseRoutineCall(0, 4);

        var expectedError = new ExpectedExpressionError(new Token[] { keyword("integer") }, new CodePosition(0, 5));

        assertNull(call);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineCall_ExpectedSecondExpression() throws InvalidLexemeException {
        var parser = createParser("main(1, integer)");
        var call = parser.tryParseRoutineCall(0, 6);

        var expectedError = new ExpectedExpressionError(new Token[] { keyword("integer") }, new CodePosition(0, 8));

        assertNull(call);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    public void testRoutineCall_ExpectedClosingParenthesis() throws InvalidLexemeException {
        var parser = createParser("main(");
        var call = parser.tryParseRoutineCall(0, 2);

        var expectedError = new ExpectedOperatorError(")", new CodePosition(0, 5));

        assertNull(call);
        assertEquals(1, parser.getErrorCount());
        assertEquals(expectedError, parser.getErrors().get(0));
    }

    private static Token identifier(String lexeme) {
        return new Token(TokenType.Identifier, lexeme);
    }

    private static Token operator(String lexeme) {
        return new Token(TokenType.Operator, lexeme);
    }

    private static Token keyword(String lexeme) {
        return new Token(TokenType.Keyword, lexeme);
    }

    private static Token literal(String lexeme) {
        return new Token(TokenType.Literal, lexeme);
    }
}
