package projectI;

import java.util.Objects;

public class StringWithLocation {
    public String getString() {
        return string;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getBeginningIndex() {
        return beginningIndex;
    }

    public StringWithLocation(String string, int lineIndex, int beginningIndex) {
        this.string = string;
        this.lineIndex = lineIndex;
        this.beginningIndex = beginningIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringWithLocation that = (StringWithLocation) o;
        return lineIndex == that.lineIndex &&
                beginningIndex == that.beginningIndex &&
                Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, lineIndex, beginningIndex);
    }

    @Override
    public String toString() {
        return "StringWithLocation{" +
                "string='" + string + '\'' +
                ", lineIndex=" + lineIndex +
                ", beginningIndex=" + beginningIndex +
                '}';
    }

    private final String string;
    private final int lineIndex;
    private final int beginningIndex;
}
