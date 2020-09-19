package projectI.AST.Primary;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.ArrayTypeNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Types.RuntimeArrayType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeRecordType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.InvalidRuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModifiablePrimaryNode implements PrimaryNode {
    public final IdentifierNode identifier;
    public final List<Accessor> accessors = new ArrayList<>();
    public final CodePosition startPosition;
    public ASTNode parent;

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    /**
     * A constructor for initializing objects of class ModifiablePrimaryNode
     * @param identifier is an identifier of the Modifiable Primary
     */
    public ModifiablePrimaryNode(IdentifierNode identifier) {
        this.identifier = identifier;
        this.startPosition = null;
    }

    /**
     * A constructor for initializing objects of class ModifiablePrimaryNode
     * @param identifier is an identifier of the Modifiable Primary
     * @param startPosition is a start position in the source code
     */
    public ModifiablePrimaryNode(IdentifierNode identifier, CodePosition startPosition) {
        this.identifier = identifier;
        this.startPosition = startPosition;
    }

    /**
     * Add an identifier to the Modifiable Primary
     * @param identifier is a new member to add
     * @return Modifiable Primary with added member
     */
    public ModifiablePrimaryNode addMember(IdentifierNode identifier) {
        accessors.add(new Member(identifier));
        return this;
    }

    /**
     * Add an expression to the Modifiable Primary
     * @param expression is an expression to add
     * @return Modifiable Primary with added expression
     */
    public ModifiablePrimaryNode addIndexer(ExpressionNode expression) {
        accessors.add(new Indexer(expression));
        return this;
    }

    /**
     * Add ArraySize instance to Modifiable Primary
     * @return Modifiable Primary itself
     */
    public ModifiablePrimaryNode addArraySize() {
        accessors.add(ArraySize.instance);
        return this;
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
        ModifiablePrimaryNode that = (ModifiablePrimaryNode) o;
        if (!identifier.equals(that.identifier)) return false;
        if (accessors.size() != that.accessors.size()) return false;

        for (int index = 0; index < accessors.size(); index++) {
            if (!accessors.get(index).equals(that.accessors.get(index)))
                return false;
        }

        return true;
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier, accessors);
    }

    /**
     * Find a position in the source code
     * @return the position
     */
    @Override
    public CodePosition getPosition() {
        return startPosition;
    }

    @Override
    public Object tryEvaluateConstant() {
        return null;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        var type = symbolTable.tryGetType(this, identifier.name);
        if (accessors.size() == 0) return type;

        for (var accessor : accessors) {
            type = accessor.getRuntimeType(type, symbolTable);
        }

        return new InvalidRuntimeType();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
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

    /**
     * Class representing an identifier of the Modifiable Primary
     */
    public abstract static class Accessor {
        public abstract boolean validate();
        public abstract RuntimeType getRuntimeType(RuntimeType modifiable, SymbolTable symbolTable);
    }

    public static final class Member extends Accessor {
        public final IdentifierNode name;

        /**
         * A constructor for initializing objects of class Member
         * @param name is a name of the instance
         */
        public Member(IdentifierNode name) {
            this.name = name;
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
            Member member = (Member) o;
            return Objects.equals(name, member.name);
        }

        /**
         * Calculate the hashcode of the object.
         * @return hashcode
         */
        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        /**
         * Get the formatted representation of the string.
         * @return the object as a string
         */
        @Override
        public String toString() {
            return "." + name;
        }

        /**
         * Check if node is valid
         * @return true if this object is valid, false otherwise.
         */
        @Override
        public boolean validate() {
            return name != null && name.validate();
        }

        @Override
        public RuntimeType getRuntimeType(RuntimeType modifiable, SymbolTable symbolTable) {
            if (modifiable instanceof RuntimeRecordType) {
                var record = (RuntimeRecordType) modifiable;

                for (var variable : record.variables) {
                    if (!variable.getValue0().equals(name.name)) continue;

                    return variable.getValue1();
                }
            }

            return new InvalidRuntimeType();
        }
    }

    public static final class Indexer extends Accessor {
        public final ExpressionNode value;

        /**
         * A constructor for initializing objects of class Indexer
         * @param value is an index to get
         */
        public Indexer(ExpressionNode value) {
            this.value = value;
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
            Indexer indexer = (Indexer) o;
            return Objects.equals(value, indexer.value);
        }

        /**
         * Calculate the hashcode of the object.
         * @return hashcode
         */
        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        /**
         * Get the formatted representation of the string.
         * @return the object as a string
         */
        @Override
        public String toString() {
            return "[" + value + "]";
        }

        /**
         * Check if node is valid
         * @return true if this object is valid, false otherwise.
         */
        @Override
        public boolean validate() {
            return value != null && value.validate();
        }

        @Override
        public RuntimeType getRuntimeType(RuntimeType modifiable, SymbolTable symbolTable) {
            if (modifiable instanceof RuntimeArrayType) {
                var array = (RuntimeArrayType) modifiable;
                return array.ElementType;
            }

            return new InvalidRuntimeType();
        }
    }

    public static final class ArraySize extends Accessor {
        public static final ArraySize instance = new ArraySize();

        private ArraySize() { }

        /**
         * Check whether this object is equal to the passed one.
         * @param obj the object to check the equality with
         * @return true if this object is equal to the passed one, false otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == ArraySize.class;
        }

        /**
         * Get the formatted representation of the string.
         * @return the object as a string
         */
        @Override
        public String toString() {
            return ".size";
        }

        /**
         * Check if node is valid
         * @return true if this object is valid, false otherwise.
         */
        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public RuntimeType getRuntimeType(RuntimeType modifiable, SymbolTable symbolTable) {
            if (modifiable instanceof RuntimeArrayType) {
                return new RuntimePrimitiveType(PrimitiveType.INTEGER);
            }

            return new InvalidRuntimeType();
        }
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
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
