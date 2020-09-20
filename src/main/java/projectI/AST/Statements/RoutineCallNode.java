package projectI.AST.Statements;

import projectI.AST.ASTNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Primary.PrimaryNode;
import projectI.AST.Types.InvalidRuntimeType;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.AST.Types.RuntimeType;
import projectI.CodePosition;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutineCallNode implements StatementNode, PrimaryNode {
    public final IdentifierNode name;
    public final List<ExpressionNode> arguments = new ArrayList<>();
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
     * Add an argument to the routine call
     * @param expression is an argument to add
     * @return Routine call with added argument
     */
    public RoutineCallNode addArgument(ExpressionNode expression) {
        arguments.add(expression);
        return this;
    }

    /**
     * A constructor for initializing objects of class RoutineCallNode
     * @param name is a name of the routine
     * @param startPosition is a start position in the source code
     */
    public RoutineCallNode(IdentifierNode name, CodePosition startPosition) {
        this.name = name;
        this.startPosition = startPosition;
    }

    /**
     * A constructor for initializing objects of class RoutineCallNode
     * @param name is a name of the routine
     */
    public RoutineCallNode(IdentifierNode name) {
        this.name = name;
        this.startPosition = null;
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
        RoutineCallNode that = (RoutineCallNode) o;
        if (arguments.size() != that.arguments.size()) return false;

        for (int index = 0; index < arguments.size(); index++) {
            if (!arguments.get(index).equals(that.arguments.get(index)))
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
        return Objects.hash(name, arguments);
    }

    /**
     * Find a start position in the source code
     * @return the position
     */
    @Override
    public CodePosition getPosition() {
        return startPosition;
    }

    @Override
    public Object tryEvaluateConstant(SymbolTable symbolTable) {
        return null;
    }

    @Override
    public RuntimeType getType(SymbolTable symbolTable) {
        var routine = symbolTable.getType(this, name.name);

        if (routine instanceof RuntimeRoutineType)
        {
            return ((RuntimeRoutineType) routine).returnType;
        }

        return InvalidRuntimeType.instance;
    }

    /**
     * Find a start position in the source code
     * @return the position
     */
    @Override
    public CodePosition getStartPosition() {
        return startPosition;
    }

    /**
     * Check if node is valid
     * @return true if this object is valid, false otherwise.
     */
    @Override
    public boolean validate() {
        if (name == null || !name.validate() || startPosition == null)
            return false;

        for (var argument : arguments) {
            if (!argument.validate())
                return false;
        }

        return true;
    }
}
