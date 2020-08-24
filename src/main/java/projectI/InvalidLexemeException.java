package projectI;

public class InvalidLexemeException extends Exception {
    public InvalidLexemeException(String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String getMessage() {
        return String.format("Lexeme '%s' is invalid.", lexeme);
    }

    private final String lexeme;
}
