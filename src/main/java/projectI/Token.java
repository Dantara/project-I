package projectI;

import java.util.Objects;

/**
 * Represents a token of a program: its type and the lexeme itself.
 */
public class Token {
    /**
     * Get the type of the token.
     * @return token's type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Get the lexeme of the token.
     * @return lexeme of the token
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Create a token with the passed type and lexeme string.
     * @param type token type
     * @param lexeme token lexeme
     */
    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    /**
     * Check whether this and the passed objects are equal.
     * @param o other object to check the equality with
     * @return true of this equals the passed object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type &&
                Objects.equals(lexeme, token.lexeme);
    }

    /**
     * Calculate the hashcode.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme);
    }

    /**
     * Get the string representation of the token.
     * @return token as string.
     */
    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}';
    }

    private final TokenType type;
    private final String lexeme;
}