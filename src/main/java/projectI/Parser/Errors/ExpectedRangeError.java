package projectI.Parser.Errors;

import projectI.CodePosition;
import projectI.Lexer.Token;

import java.util.Arrays;
import java.util.Objects;

public class ExpectedRangeError extends ParsingError{
    public final Token[] tokens;
    public final CodePosition end;

    public ExpectedRangeError(Token[] tokens, CodePosition position, CodePosition end) {
        super(String.format("Expected range but got '%s'.", tokensAsString(tokens)), position);
        this.tokens = tokens;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedRangeError that = (ExpectedRangeError) o;
        return Arrays.equals(tokens, that.tokens) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), end);
        result = 31 * result + Arrays.hashCode(tokens);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "..(" + end.lineIndex + "; " + end.beginningIndex + ")";
    }
}
