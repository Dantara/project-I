package projectI.Parser.Errors;

import projectI.CodePosition;
import projectI.Lexer.Token;

import java.util.Arrays;
import java.util.Objects;

public class ExpectedVariableDeclarationError extends ParsingError {
    public final Token[] tokens;
    public final CodePosition end;

    public ExpectedVariableDeclarationError(Token[] tokens, CodePosition position, CodePosition end) {
        super(String.format("Expected variable declaration but got %s.", tokensAsString(tokens)), position);
        this.tokens = tokens;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedVariableDeclarationError that = (ExpectedVariableDeclarationError) o;
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
