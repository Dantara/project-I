package projectI.AST.Expressions;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.AST.Types.InvalidRuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SummandNode implements ASTNode {
    public final FactorNode factor;
    public final List<OperatorWithNode<MultiplicationOperator, FactorNode>> otherFactors = new ArrayList<>();
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
     * A constructor for initializing objects of class SummandNode
     * @param factor is a factor to multiply or divide in the summand
     */
    public SummandNode(FactorNode factor) {
        this.factor = factor;
    }

    /**
     * Add a factor to the list of factors with its operator
     * @param operator is a operator of added factor
     * @param factor is a factor to add
     * @return Summand Node itself
     */
    public SummandNode addFactor(MultiplicationOperator operator, FactorNode factor) {
        otherFactors.add(new OperatorWithNode<>(operator, factor));
        return this;
    }

    /**
     * Add a factor to the list of factors with its operator
     * @param operator is a operator of added factor
     * @param factor is a factor to add
     * @param operatorPosition is a position of the operator in the source code
     * @return Summand Node itself
     */
    public SummandNode addFactor(MultiplicationOperator operator, FactorNode factor, CodePosition operatorPosition) {
        otherFactors.add(new OperatorWithNode<>(operator, factor, operatorPosition));
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
        SummandNode that = (SummandNode) o;
        if (!factor.equals(that.factor)) return false;
        if (otherFactors.size() != that.otherFactors.size()) return false;

        for (int index = 0; index < otherFactors.size(); index++) {
            if (!otherFactors.get(index).equals(that.otherFactors.get(index)))
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
        return Objects.hash(factor, otherFactors);
    }

    /**
     * Get the formatted representation of the string.
     * @return the object as a string
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(factor);
        builder.append(" ");

        for (var other : otherFactors) {
            builder.append(other.operator);
            builder.append(" ");
            builder.append(other.node);
            builder.append(" ");
        }

        return builder.toString();
    }

    /**
     * Find a position in the source code
     * @return the position
     */
    public CodePosition getPosition() {
        return factor.getPosition();
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        if (factor == null || !factor.validate())
            return false;

        for (var factor : otherFactors) {
            if (factor == null)
                return false;

            if (factor.operator == null || factor.node == null
                    || !factor.node.validate() || factor.operatorPosition == null)
                return false;
        }

        return true;
    }

    public Object tryEvaluateConstant() {
        var value = factor.tryEvaluateConstant();
        if (value == null) return null;
        if (otherFactors.size() == 0) return value;
        if (value instanceof Boolean) return null;

        for (var otherFactor : otherFactors) {
            var otherValue = otherFactor.node.tryEvaluateConstant();
            if (otherValue == null) return null;
            if (otherValue instanceof Boolean) return null;

            if (value instanceof Double && otherValue instanceof Integer) {
                otherValue = Double.valueOf((Integer) otherValue);
            }

            if (value instanceof Integer && otherValue instanceof Double) {
                value = Double.valueOf((Integer) value);
            }

            if (value instanceof Integer && otherValue instanceof Integer) {
                value = switch (otherFactor.operator) {
                    case DIVIDE -> (Integer) value / (Integer) otherValue;
                    case MULTIPLY -> (Integer) value * (Integer) otherValue;
                    case MODULO -> (Integer) value % (Integer) otherValue;
                };


                continue;
            }

            if (value instanceof Double && otherValue instanceof Double) {
                value = switch (otherFactor.operator) {
                    case DIVIDE -> (Double) value / (Double) otherValue;
                    case MULTIPLY -> (Double) value * (Double) otherValue;
                    case MODULO -> null;
                };
            }

            if (value == null)
                return null;
        }

        return null;
    }

    public RuntimeType getType(SymbolTable symbolTable) {
        if (otherFactors.size() == 0) return factor.getType(symbolTable);

        var type = factor.getType(symbolTable);
        if (!(type instanceof RuntimePrimitiveType)) return InvalidRuntimeType.instance;
        var primitiveType = (RuntimePrimitiveType) type;

        for (var otherFactor : otherFactors) {
            var otherType = otherFactor.node.getType(symbolTable);
            if (!(otherType instanceof RuntimePrimitiveType)) return InvalidRuntimeType.instance;
            var otherPrimitiveType = (RuntimePrimitiveType) otherType;
            if (primitiveType == otherPrimitiveType) continue;

            if (primitiveType.type == PrimitiveType.INTEGER && otherPrimitiveType.type != PrimitiveType.REAL ||
                    primitiveType.type == PrimitiveType.REAL && otherPrimitiveType.type != PrimitiveType.INTEGER)
                primitiveType = new RuntimePrimitiveType(PrimitiveType.REAL);

            if (primitiveType.type == PrimitiveType.INTEGER && otherPrimitiveType.type != PrimitiveType.BOOLEAN ||
                    primitiveType.type == PrimitiveType.BOOLEAN && otherPrimitiveType.type != PrimitiveType.INTEGER)
                primitiveType = new RuntimePrimitiveType(PrimitiveType.INTEGER);

            if (primitiveType.type == PrimitiveType.REAL && otherPrimitiveType.type != PrimitiveType.BOOLEAN ||
                    primitiveType.type == PrimitiveType.BOOLEAN && otherPrimitiveType.type != PrimitiveType.REAL)
                primitiveType = new RuntimePrimitiveType(PrimitiveType.REAL);
        }

        return primitiveType;
    }
}
