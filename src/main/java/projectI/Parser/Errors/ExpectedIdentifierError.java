package projectI.Parser.Errors;

import projectI.CodePosition;
import projectI.Lexer.Token;

import java.util.Objects;

public class ExpectedIdentifierError extends ParsingError {
    public final Token token;

    public ExpectedIdentifierError(Token token, CodePosition position) {
        super(String.format("Expected an identifier but got %s (%s).", token.getLexeme(), token.getType()), position);
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedIdentifierError that = (ExpectedIdentifierError) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token);
    }
}
