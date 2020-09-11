package projectI.AST.Expressions;

import projectI.CodePosition;

import java.util.Objects;

public class OperatorWithNode<EOperator, ENode> {
    public final EOperator operator;
    public final ENode node;
    public final CodePosition operatorPosition;

    /**
     * A constructor for initializing objects of class OperatorWithNode
     * @param operator is an operator for initializing
     * @param node is a node for initializing
     * @param operatorPosition is a position of an operator in the source code
     */
    public OperatorWithNode(EOperator operator, ENode node, CodePosition operatorPosition) {
        this.operator = operator;
        this.node = node;
        this.operatorPosition = operatorPosition;
    }

    /**
     * A constructor for initializing objects of class OperatorWithNode
     * @param operator is an operator for initializing
     * @param node is a node for initializing
     */
    public OperatorWithNode(EOperator operator, ENode node) {
        this.operator = operator;
        this.node = node;
        this.operatorPosition = null;
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
        OperatorWithNode<?, ?> that = (OperatorWithNode<?, ?>) o;
        return Objects.equals(operator, that.operator) &&
                Objects.equals(node, that.node);
    }

    /**
     * Calculate the hashcode of the object.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(operator, node);
    }
}
