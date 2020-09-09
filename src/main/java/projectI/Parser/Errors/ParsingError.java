package projectI.Parser.Errors;

import projectI.CodePosition;
import projectI.Lexer.Token;

import java.util.Objects;

public class ParsingError {
    public final String message;
    public final CodePosition position;

    public ParsingError(String message, CodePosition position) {
        this.message = message;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsingError that = (ParsingError) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, position);
    }

    @Override
    public String toString() {
        return message + " " + position;
    }

    protected static String tokensAsString(Token[] tokens) {
        var builder = new StringBuilder();

        for (int index = 0; index < tokens.length; index++) {
            builder.append(tokens[index].getLexeme());

            if (index != tokens.length - 1)
                builder.append(' ');
        }

        return builder.toString();
    }
}
