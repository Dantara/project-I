package projectI.Parser.Errors;

import projectI.CodePosition;
import projectI.Lexer.Token;

import java.util.Arrays;

public class ExpectedExpressionError extends ParsingError {
    public final Token[] tokens;

    public ExpectedExpressionError(Token[] tokens, CodePosition position) {
        super(String.format("Expected expression but got %s.", tokensAsString(tokens)), position);
        this.tokens = tokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedExpressionError that = (ExpectedExpressionError) o;
        return Arrays.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(tokens);
        return result;
    }
}
