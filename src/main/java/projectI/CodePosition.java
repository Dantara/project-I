package projectI;

import java.util.Objects;

public class CodePosition implements Comparable<CodePosition> {
    public final int lineIndex;
    public final int beginningIndex;

    public CodePosition(int lineIndex, int beginningIndex) {
        this.lineIndex = lineIndex;
        this.beginningIndex = beginningIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodePosition that = (CodePosition) o;
        return lineIndex == that.lineIndex &&
                beginningIndex == that.beginningIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineIndex, beginningIndex);
    }

    @Override
    public String toString() {
        return "(" + lineIndex + "; " + beginningIndex + ")";
    }

    @Override
    public int compareTo(CodePosition o) {
        var linesComparison = lineIndex - o.lineIndex;
        if (linesComparison != 0)
            return linesComparison;

        return beginningIndex - o.beginningIndex;
    }
}
