package projectI.Parser.Errors;

import projectI.CodePosition;

import java.util.Objects;

public class ExpectedKeywordError extends ParsingError {
    public final String expectedKeyword;

    public ExpectedKeywordError(String expectedKeyword, CodePosition position) {
        super(String.format("Expected keyword '%s'.", expectedKeyword), position);
        this.expectedKeyword = expectedKeyword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedKeywordError that = (ExpectedKeywordError) o;
        return Objects.equals(expectedKeyword, that.expectedKeyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expectedKeyword);
    }
}
