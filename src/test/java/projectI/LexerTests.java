package projectI;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;

public class LexerTests extends TestCase {
    public LexerTests(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(LexerTests.class);
    }

    private Lexer lexer;

    @Override
    protected void setUp() {
        lexer = new Lexer();
    }

    public void testArraysAndFor() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/arrays_and_for.txt");
        assertArrayEquals(new Token[] {
                keyword("var"),
                identifier("a"),
                keyword("is"),
                keyword("array"),
                operator("["),
                literal("10"),
                operator("]"),
                keyword("integer"),
                newLine(), newLine(),

                keyword("routine"),
                identifier("get_sum"),
                operator("("),
                identifier("arr"),
                operator(":"),
                keyword("array"),
                operator("["),
                operator("]"),
                keyword("integer"),
                operator(")"),
                operator(":"),
                keyword("integer"),
                keyword("is"),
                newLine(),
                keyword("var"),
                identifier("result"),
                keyword("is"),
                literal("0"),
                newLine(), newLine(),

                keyword("for"),
                identifier("i"),
                keyword("in"),
                literal("1"),
                operator(".."),
                identifier("arr"),
                operator("."),
                keyword("size"),
                keyword("loop"),
                newLine(),

                identifier("result"),
                operator(":="),
                identifier("result"),
                operator("+"),
                identifier("arr"),
                operator("["),
                identifier("i"),
                operator("]"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("return"),
                identifier("result"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("sum"),
                keyword("is"),
                literal("0"),
                newLine(), newLine(),

                keyword("for"),
                identifier("i"),
                keyword("in"),
                literal("1"),
                operator(".."),
                literal("10"),
                keyword("loop"),
                newLine(),

                identifier("sum"),
                operator(":="),
                identifier("sum"),
                operator("+"),
                identifier("i"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("for"),
                identifier("i"),
                keyword("in"),
                keyword("reverse"),
                literal("1"),
                operator(".."),
                identifier("a"),
                operator("."),
                keyword("size"),
                keyword("loop"),
                newLine(),

                identifier("sum"),
                operator(":="),
                identifier("sum"),
                operator("+"),
                identifier("i"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end"),
        }, tokens);
    }

    public void testBasic() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/basic.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                operator(":"),
                keyword("integer"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                newLine(),

                keyword("var"),
                identifier("b"),
                keyword("is"),
                literal("2"),
                newLine(),

                keyword("var"),
                identifier("c"),
                keyword("is"),
                identifier("a"),
                operator("+"),
                identifier("b"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testConditional() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/conditional.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                keyword("is"),
                literal("1"),
                newLine(),

                keyword("var"),
                identifier("b"),
                keyword("is"),
                literal("2"),
                newLine(),

                keyword("var"),
                identifier("c"),
                operator(":"),
                keyword("real"),
                newLine(), newLine(),

                keyword("if"),
                identifier("a"),
                operator("="),
                literal("1"),
                operator("and"),
                identifier("b"),
                operator("="),
                literal("2"),
                keyword("then"),
                newLine(),

                identifier("c"),
                operator(":="),
                literal("1.0"),
                newLine(),

                keyword("else"),
                newLine(),

                identifier("c"),
                operator(":="),
                literal("2.0"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testDenseCode() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/dense_code.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                operator(":"),
                keyword("integer"),
                newLine(), newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("3"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("3"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("4"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("3"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("4"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("5"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("3"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("4"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("3"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                semicolon(),
                identifier("a"),
                operator(":="),
                literal("2"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testRecords() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/records.txt");
        assertArrayEquals(new Token[] {
                keyword("type"),
                identifier("vector"),
                keyword("is"),
                keyword("record"),
                newLine(),

                keyword("var"),
                identifier("x"),
                operator(":"),
                keyword("real"),
                keyword("is"),
                literal("0"),
                newLine(),

                keyword("var"),
                identifier("y"),
                operator(":"),
                keyword("real"),
                keyword("is"),
                literal("0"),
                newLine(),

                keyword("var"),
                identifier("z"),
                operator(":"),
                keyword("real"),
                keyword("is"),
                literal("0"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("routine"),
                identifier("add"),
                operator("("),
                identifier("v1"),
                operator(":"),
                identifier("vector"),
                operator(","),
                identifier("v2"),
                operator(":"),
                identifier("vector"),
                operator(")"),
                operator(":"),
                identifier("vector"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("result"),
                operator(":"),
                identifier("vector"),
                newLine(), newLine(),

                identifier("result"),
                operator("."),
                identifier("x"),
                operator(":="),
                identifier("v1"),
                operator("."),
                identifier("x"),
                operator("+"),
                identifier("v2"),
                operator("."),
                identifier("x"),
                newLine(),

                identifier("result"),
                operator("."),
                identifier("y"),
                operator(":="),
                identifier("v1"),
                operator("."),
                identifier("y"),
                operator("+"),
                identifier("v2"),
                operator("."),
                identifier("y"),
                newLine(),

                identifier("result"),
                operator("."),
                identifier("z"),
                operator(":="),
                identifier("v1"),
                operator("."),
                identifier("z"),
                operator("+"),
                identifier("v2"),
                operator("."),
                identifier("z"),
                newLine(), newLine(),

                keyword("return"),
                identifier("result"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("v1"),
                operator(":"),
                identifier("vector"),
                newLine(),

                identifier("v1"),
                operator("."),
                identifier("y"),
                operator(":="),
                literal("1"),
                newLine(), newLine(),

                keyword("var"),
                identifier("v2"),
                operator(":"),
                identifier("vector"),
                newLine(),

                identifier("v2"),
                operator("."),
                identifier("x"),
                operator(":="),
                operator("-"),
                literal("2"),
                newLine(),

                identifier("v2"),
                operator("."),
                identifier("z"),
                operator(":="),
                literal("2"),
                newLine(), newLine(),

                keyword("var"),
                identifier("v3"),
                keyword("is"),
                identifier("add"),
                operator("("),
                identifier("v1"),
                operator(","),
                identifier("v2"),
                operator(")"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testRoutineCall() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/routine_call.txt");
        assertArrayEquals(new Token[] {
                keyword("var"),
                identifier("g"),
                keyword("is"),
                literal("1"),
                newLine(),
                newLine(),

                keyword("routine"),
                identifier("add"),
                operator("("),
                identifier("a"),
                operator(":"),
                keyword("integer"),
                operator(","),
                identifier("b"),
                operator(":"),
                keyword("integer"),
                operator(")"),
                operator(":"),
                keyword("integer"),
                keyword("is"),
                newLine(),

                keyword("return"),
                identifier("a"),
                operator("+"),
                identifier("b"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("routine"),
                identifier("increment_g"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                identifier("g"),
                operator(":="),
                identifier("g"),
                operator("+"),
                literal("1"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                identifier("increment_g"),
                newLine(),

                keyword("var"),
                identifier("sum"),
                keyword("is"),
                identifier("add"),
                operator("("),
                identifier("g"),
                operator(","),
                literal("1"),
                operator(")"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testTypeConversion() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/type_conversion.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                operator(":"),
                keyword("integer"),
                newLine(),

                keyword("var"),
                identifier("b"),
                operator(":"),
                keyword("real"),
                newLine(),

                keyword("var"),
                identifier("c"),
                operator(":"),
                keyword("boolean"),
                newLine(), newLine(),

                identifier("a"),
                operator(":="),
                literal("1"),
                newLine(),

                identifier("a"),
                operator(":="),
                literal("0.0"),
                newLine(),

                identifier("a"),
                operator(":="),
                keyword("true"),
                newLine(), newLine(),

                identifier("b"),
                operator(":="),
                literal("1"),
                newLine(),

                identifier("b"),
                operator(":="),
                literal("0.0"),
                newLine(),

                identifier("b"),
                operator(":="),
                keyword("false"),
                newLine(), newLine(),

                identifier("c"),
                operator(":="),
                keyword("true"),
                newLine(),

                identifier("c"),
                operator(":="),
                literal("1"),
                newLine(), newLine(),

                keyword("if"),
                identifier("a"),
                keyword("then"),
                newLine(),

                identifier("c"),
                operator(":="),
                keyword("false"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testWhileLoop() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/while_loop.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                keyword("is"),
                literal("0"),
                newLine(), newLine(),

                keyword("while"),
                identifier("a"),
                operator("<"),
                literal("10"),
                keyword("loop"),
                newLine(),

                identifier("a"),
                operator(":="),
                identifier("a"),
                operator("+"),
                literal("1"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testOperators() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/operators.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                keyword("is"),
                literal("5.0"),
                operator("*"),
                literal("15"),
                newLine(),

                keyword("var"),
                identifier("b"),
                keyword("is"),
                literal("1.0"),
                operator("<"),
                literal("5.0"),
                newLine(),

                keyword("var"),
                identifier("c"),
                keyword("is"),
                literal("1"),
                operator(">="),
                literal("5"),
                newLine(),

                keyword("var"),
                identifier("d"),
                keyword("is"),
                literal("1"),
                operator(">"),
                literal("5"),
                newLine(),

                keyword("var"),
                identifier("e"),
                keyword("is"),
                literal("1"),
                operator("<="),
                literal("5"),
                newLine(),

                keyword("var"),
                identifier("f"),
                keyword("is"),
                keyword("true"),
                operator("and"),
                keyword("true"),
                newLine(),

                keyword("var"),
                identifier("g"),
                keyword("is"),
                keyword("true"),
                operator("xor"),
                keyword("false"),
                newLine(),

                keyword("var"),
                identifier("h"),
                keyword("is"),
                operator("not"),
                keyword("false"),
                newLine(),

                keyword("var"),
                identifier("i"),
                keyword("is"),
                keyword("true"),
                operator("or"),
                keyword("false"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testBadFormatting() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/bad_formatting.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                keyword("is"),
                literal("1"),
                newLine(),

                keyword("for"),
                identifier("i"),
                keyword("in"),
                literal("1"),
                operator(".."),
                literal("10"),
                keyword("loop"),
                newLine(),

                keyword("if"),
                identifier("a"),
                operator("%"),
                literal("2"),
                operator("/="),
                literal("0"),
                keyword("then"),
                newLine(),

                identifier("a"),
                operator(":="),
                identifier("a"),
                operator("+"),
                identifier("i"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testTypeSynonym() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/type_synonym.txt");
        assertArrayEquals(new Token[] {
                keyword("type"),
                identifier("arr8"),
                keyword("is"),
                keyword("array"),
                operator("["),
                literal("8"),
                operator("]"),
                keyword("integer"),
                newLine(),
                newLine(),

                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("a"),
                operator(":"),
                identifier("arr8"),
                newLine(),

                identifier("a"),
                operator("["),
                literal("0"),
                operator("]"),
                operator(":="),
                identifier("a"),
                operator("."),
                keyword("size"),
                newLine(),

                keyword("end")
        }, tokens);
    }

    public void testNestedLoop() throws InvalidLexemeException, IOException {
        var tokens = scanFile("code_examples/nested_loop.txt");
        assertArrayEquals(new Token[] {
                keyword("routine"),
                identifier("main"),
                operator("("),
                operator(")"),
                keyword("is"),
                newLine(),

                keyword("var"),
                identifier("counter"),
                keyword("is"),
                literal("0"),
                newLine(), newLine(),

                keyword("for"),
                identifier("i"),
                keyword("in"),
                literal("1"),
                operator(".."),
                literal("6"),
                keyword("loop"),
                newLine(),

                keyword("for"),
                identifier("j"),
                keyword("in"),
                literal("2"),
                operator(".."),
                literal("7"),
                keyword("loop"),
                newLine(),

                identifier("counter"),
                operator(":="),
                identifier("i"),
                operator("*"),
                identifier("j"),
                newLine(),

                keyword("end"),
                newLine(),

                keyword("end"),
                newLine(), newLine(),

                keyword("end")
        }, tokens);
    }

    private Token[] scanFile(String path) throws InvalidLexemeException, IOException {
        var programText = Files.readString(Path.of(path));
        return lexer.scan(programText);
    }

    private static Token keyword(String lexeme) {
        return new Token(TokenType.Keyword, lexeme);
    }

    private static Token identifier(String lexeme) {
        return new Token(TokenType.Identifier, lexeme);
    }

    private static Token operator(String lexeme) {
        return new Token(TokenType.Operator, lexeme);
    }

    private static Token declarationSeparator(String lexeme) {
        return new Token(TokenType.DeclarationSeparator, lexeme);
    }

    private static Token literal(String lexeme) {
        return new Token(TokenType.Literal, lexeme);
    }

    private static Token newLine() {
        return declarationSeparator("\n");
    }

    private static Token semicolon() {
        return declarationSeparator(";");
    }
}