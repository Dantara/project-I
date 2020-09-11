package projectI.Parser.Errors;

import projectI.CodePosition;
import projectI.Lexer.Token;

import java.util.Arrays;

public class ExpectedModifiablePrimaryError extends ParsingError {
    public final Token[] tokens;

    public ExpectedModifiablePrimaryError(Token[] tokens, CodePosition position) {
        super(String.format("Expected modifiable primary but got %s.", tokensAsString(tokens)), position);
        this.tokens = tokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedModifiablePrimaryError that = (ExpectedModifiablePrimaryError) o;
        return Arrays.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(tokens);
        return result;
    }
}
