package projectI.Parser.Errors;

import projectI.CodePosition;

import java.util.Objects;

public class ExpectedOperatorError extends ParsingError {
    public final String operator;

    public ExpectedOperatorError(String operator, CodePosition position) {
        super(String.format("Expected operator '%s'.", operator), position);
        this.operator = operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedOperatorError that = (ExpectedOperatorError) o;
        return Objects.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operator);
    }
}
