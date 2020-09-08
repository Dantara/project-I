package projectI.AST.Primary;

import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Declarations.IdentifierNode;
import projectI.CodePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModifiablePrimaryNode implements PrimaryNode {
    public final IdentifierNode identifier;
    public final List<Accessor> accessors = new ArrayList<>();
    public final CodePosition startPosition;

    public ModifiablePrimaryNode(IdentifierNode identifier) {
        this.identifier = identifier;
        this.startPosition = null;
    }

    public ModifiablePrimaryNode(IdentifierNode identifier, CodePosition startPosition) {
        this.identifier = identifier;
        this.startPosition = startPosition;
    }

    public ModifiablePrimaryNode addMember(IdentifierNode identifier) {
        accessors.add(new Member(identifier));
        return this;
    }

    public ModifiablePrimaryNode addIndexer(ExpressionNode expression) {
        accessors.add(new Indexer(expression));
        return this;
    }

    public ModifiablePrimaryNode addArraySize() {
        accessors.add(ArraySize.instance);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifiablePrimaryNode that = (ModifiablePrimaryNode) o;
        if (!identifier.equals(that.identifier)) return false;
        if (accessors.size() != that.accessors.size()) return false;

        for (int index = 0; index < accessors.size(); index++) {
            if (!accessors.get(index).equals(that.accessors.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, accessors);
    }

    @Override
    public CodePosition getPosition() {
        return startPosition;
    }

    @Override
    public boolean validate() {
        if (identifier == null || !identifier.validate() || startPosition == null)
            return false;

        for (var accessor : accessors) {
            if (accessor == null)
                return false;

            if (!accessor.validate())
                return false;
        }

        return true;
    }

    public abstract static class Accessor {
        public abstract boolean validate();
    }

    public static final class Member extends Accessor {
        public final IdentifierNode name;

        public Member(IdentifierNode name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Member member = (Member) o;
            return Objects.equals(name, member.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "." + name;
        }

        @Override
        public boolean validate() {
            return name != null && name.validate();
        }
    }

    public static final class Indexer extends Accessor {
        public final ExpressionNode value;

        public Indexer(ExpressionNode value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Indexer indexer = (Indexer) o;
            return Objects.equals(value, indexer.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "[" + value + "]";
        }

        @Override
        public boolean validate() {
            return value != null && value.validate();
        }
    }

    public static final class ArraySize extends Accessor {
        public static final ArraySize instance = new ArraySize();

        private ArraySize() { }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == ArraySize.class;
        }

        @Override
        public String toString() {
            return ".size";
        }

        @Override
        public boolean validate() {
            return true;
        }
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(identifier);

        for (var accessor : accessors) {
            builder.append(accessor);
        }

        return builder.toString();
    }
}
