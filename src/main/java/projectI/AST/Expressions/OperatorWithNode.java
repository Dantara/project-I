package projectI.AST.Expressions;

import projectI.CodePosition;

import java.util.Objects;

public class OperatorWithNode<EOperator, ENode> {
    public final EOperator operator;
    public final ENode node;
    public final CodePosition operatorPosition;

    public OperatorWithNode(EOperator operator, ENode node, CodePosition operatorPosition) {
        this.operator = operator;
        this.node = node;
        this.operatorPosition = operatorPosition;
    }

    public OperatorWithNode(EOperator operator, ENode node) {
        this.operator = operator;
        this.node = node;
        this.operatorPosition = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatorWithNode<?, ?> that = (OperatorWithNode<?, ?>) o;
        return Objects.equals(operator, that.operator) &&
                Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, node);
    }
}
