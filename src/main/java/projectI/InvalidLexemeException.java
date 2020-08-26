package projectI;

/**
 * Exception that is thrown when the lexer cannot recognize a lexeme.
 */
public class InvalidLexemeException extends Exception {
    /**
     * Get the lexeme that cause the exception.
     * @return lexeme with location
     */
    public StringWithLocation getLexeme() {
        return lexeme;
    }

    /**
     * Create an exception for the passed invalid lexeme.
     * @param lexeme invalid lexeme
     */
    public InvalidLexemeException(StringWithLocation lexeme) {
        this.lexeme = lexeme;
    }

    /**
     * Get a formatted message for the invalid lexeme.
     * @return error message
     */
    @Override
    public String getMessage() {
        return String.format("Lexeme '%s' at (%d, %d) is invalid.", lexeme.getString(), lexeme.getLineIndex(), lexeme.getBeginningIndex());
    }

    private final StringWithLocation lexeme;
}
