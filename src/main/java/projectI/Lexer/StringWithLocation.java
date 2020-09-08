package projectI.Lexer;

import projectI.CodePosition;

import java.util.Objects;

/**
 * Represents a lexeme at a particular location in source code.
 */
public class StringWithLocation {
    /**
     * Get the string.
     * @return string
     */
    public String getString() {
        return string;
    }

    /**
     * Get the line index where the string resides.
     * @return line index
     */
    public int getLineIndex() {
        return position.lineIndex;
    }

    /**
     * Get the index of the character where the string begins.
     * @return beginning index
     */
    public int getBeginningIndex() {
        return position.beginningIndex;
    }

    /**
     * Get the position of the string in source code.
     * @return position
     */
    public CodePosition getPosition() {
        return position;
    }

    /**
     * Create a string at some location in source code.
     * @param string string
     * @param lineIndex line index of the string
     * @param beginningIndex character index where the string begins
     */
    public StringWithLocation(String string, int lineIndex, int beginningIndex) {
        this.string = string;
        this.position = new CodePosition(lineIndex, beginningIndex);
    }

    /**
     * Check whether this object is equal to the passed one.
     * @param o the object to check the equality with
     * @return true if this object is equal to the passed one, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringWithLocation that = (StringWithLocation) o;
        return Objects.equals(position, that.position) &&
                Objects.equals(string, that.string);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(string, position);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        return "StringWithLocation{" +
                "string='" + string + '\'' +
                ", lineIndex=" + position.lineIndex +
                ", beginningIndex=" + position.beginningIndex +
                '}';
    }

    private final String string;
    private final CodePosition position;
}
