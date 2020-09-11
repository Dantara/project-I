package projectI.Parser.Errors;

import projectI.CodePosition;

import java.util.Objects;

public class ExpectedDeclarationError extends ParsingError {
    public final CodePosition end;

    public ExpectedDeclarationError(CodePosition position, CodePosition end) {
        super("Expected declaration.", position);
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExpectedDeclarationError that = (ExpectedDeclarationError) o;
        return Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), end);
    }

    @Override
    public String toString() {
        return super.toString() + "..(" + end.lineIndex + "; " + end.beginningIndex + ")";
    }
}
