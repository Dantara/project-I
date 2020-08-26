package projectI;

import static org.junit.Assert.assertArrayEquals;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LexerLocationsTest extends TestCase {
    public LexerLocationsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(LexerLocationsTest.class);
    }

    private Lexer lexer;

    @Override
    protected void setUp() {
        lexer = new Lexer();
    }

    public void testLocationsBasic() throws InvalidLexemeException, IOException{
        var stringsWithLocations = scanFile("code_examples/basic.txt");
        assertArrayEquals(new StringWithLocation[] {
                new StringWithLocation("routine", 0, 0),
                new StringWithLocation("main", 0, 8),
                new StringWithLocation("(", 0, 12),
                new StringWithLocation(")", 0, 13),
                new StringWithLocation("is", 0, 15),
                new StringWithLocation("\n", 0, 17),

                new StringWithLocation("var", 1, 4),
                new StringWithLocation("a", 1, 8),
                new StringWithLocation(":", 1, 9),
                new StringWithLocation("integer", 1, 11),
                new StringWithLocation("\n", 1, 18),

                new StringWithLocation("a", 2, 4),
                new StringWithLocation(":=", 2, 6),
                new StringWithLocation("1", 2, 9),
                new StringWithLocation("\n", 2, 10),

                new StringWithLocation("var", 3, 4),
                new StringWithLocation("b", 3, 8),
                new StringWithLocation("is", 3, 10),
                new StringWithLocation("2", 3, 13),
                new StringWithLocation("\n", 3, 14),

                new StringWithLocation("var", 4, 4),
                new StringWithLocation("c", 4, 8),
                new StringWithLocation("is", 4, 10),
                new StringWithLocation("a", 4, 13),
                new StringWithLocation("+", 4, 15),
                new StringWithLocation("b", 4, 17),
                new StringWithLocation("\n", 4, 18),

                new StringWithLocation("end", 5, 0)
        }, stringsWithLocations);
    }

    public void testWhileLoop() throws InvalidLexemeException, IOException {
        var stringsWithLocations = scanFile("code_examples/while_loop.txt");
        assertArrayEquals(new StringWithLocation[] {
                new StringWithLocation("routine", 0, 0),
                new StringWithLocation("main", 0, 8),
                new StringWithLocation("(", 0, 12),
                new StringWithLocation(")", 0, 13),
                new StringWithLocation("is", 0, 15),
                new StringWithLocation("\n", 0, 17),

                new StringWithLocation("var", 1, 4),
                new StringWithLocation("a", 1, 8),
                new StringWithLocation("is", 1, 10),
                new StringWithLocation("0", 1, 13),
                new StringWithLocation("\n", 1, 14),

                new StringWithLocation("\n", 2, 0),

                new StringWithLocation("while", 3, 4),
                new StringWithLocation("a", 3, 10),
                new StringWithLocation("<", 3, 12),
                new StringWithLocation("10", 3, 14),
                new StringWithLocation("loop", 3, 17),
                new StringWithLocation("\n", 3, 21),

                new StringWithLocation("a", 4, 8),
                new StringWithLocation(":=", 4, 10),
                new StringWithLocation("a", 4, 13),
                new StringWithLocation("+", 4, 15),
                new StringWithLocation("1", 4, 17),
                new StringWithLocation("\n", 4, 18),

                new StringWithLocation("end", 5, 4),
                new StringWithLocation("\n", 5, 7),

                new StringWithLocation("end", 6, 0)
        }, stringsWithLocations);
    }

    public void testBadFormatting() throws InvalidLexemeException, IOException {
        var stringsWithLocations = scanFile("code_examples/bad_formatting.txt");
        assertArrayEquals(new StringWithLocation[] {
                new StringWithLocation("routine", 0, 0),
                new StringWithLocation("main", 0, 8),
                new StringWithLocation("(", 0, 12),
                new StringWithLocation(")", 0, 13),
                new StringWithLocation("is", 0, 15),
                new StringWithLocation("\n", 0, 17),

                new StringWithLocation("var", 1, 0),
                new StringWithLocation("a", 1, 4),
                new StringWithLocation("is", 1, 6),
                new StringWithLocation("1", 1, 9),
                new StringWithLocation("\n", 1, 10),

                new StringWithLocation("for", 2, 0),
                new StringWithLocation("i", 2, 4),
                new StringWithLocation("in", 2, 6),
                new StringWithLocation("1", 2, 9),
                new StringWithLocation("..", 2, 10),
                new StringWithLocation("10", 2, 12),
                new StringWithLocation("loop", 2, 15),
                new StringWithLocation("\n", 2, 19),

                new StringWithLocation("if", 3, 0),
                new StringWithLocation("a", 3, 3),
                new StringWithLocation("%", 3, 4),
                new StringWithLocation("2", 3, 5),
                new StringWithLocation("/=", 3, 6),
                new StringWithLocation("0", 3, 8),
                new StringWithLocation("then", 3, 10),
                new StringWithLocation("\n", 3, 14),

                new StringWithLocation("a", 4, 0),
                new StringWithLocation(":=", 4, 1),
                new StringWithLocation("a", 4, 3),
                new StringWithLocation("+", 4, 4),
                new StringWithLocation("i", 4, 5),
                new StringWithLocation("\n", 4, 6),

                new StringWithLocation("end", 5, 0),
                new StringWithLocation("\n", 5, 3),

                new StringWithLocation("end", 6, 0),
                new StringWithLocation("\n", 6, 3),

                new StringWithLocation("end", 7, 0),
        }, stringsWithLocations);
    }

    private StringWithLocation[] scanFile(String path) throws InvalidLexemeException, IOException {
        var programText = Files.readString(Path.of(path));
        lexer.scan(programText);
        return lexer.getLexemesWithLocations();
    }
}
