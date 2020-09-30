package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Declarations.PrimitiveTypeNode;
import projectI.AST.Expressions.*;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import static org.objectweb.asm.Opcodes.*;
import static projectI.AST.Declarations.PrimitiveType.*;
import static projectI.AST.Declarations.PrimitiveType.INTEGER;

public class ExpressionCodeGenerator {
    private final MethodVisitor methodVisitor;
    private final ExpressionNode expression;
    private final VariableContext variableContext;
    private final SymbolTable symbolTable;

    public ExpressionCodeGenerator(MethodVisitor methodVisitor, ExpressionNode expression, VariableContext variableContext, SymbolTable symbolTable) {
        this.methodVisitor = methodVisitor;
        this.expression = expression;
        this.variableContext = variableContext;
        this.symbolTable = symbolTable;
    }

    public RuntimeType generate() {
        var constant = expression.tryEvaluateConstant(symbolTable);

        if (constant != null) {
            methodVisitor.visitLdcInsn(constant);
        } else {
            generate(expression.relation);

            if (expression.otherRelations.size() > 0)
                throw new IllegalStateException();
        }

        return expression.getType(symbolTable);
    }

    private void generate(RelationNode relation) {
        if (relation instanceof BinaryRelationNode) {
            generate((BinaryRelationNode) relation);
        } else if (relation instanceof NegatedRelationNode) {
            generate((NegatedRelationNode) relation);
        } else {
            throw new IllegalStateException();
        }
    }

    private void generate(BinaryRelationNode relation) {
        generate(relation.simple);

        if (relation.otherSimple != null)
            throw new IllegalStateException();
    }

    private void generate(NegatedRelationNode relation) {
        methodVisitor.visitLdcInsn(1);
        generate(relation.innerRelation);
        methodVisitor.visitLdcInsn(ISUB);
    }

    private void generate(SimpleNode simple) {
        generate(simple.summand);
        if (simple.otherSummands.size() == 0) return;

        var summandType = ((RuntimePrimitiveType) simple.summand.getType(symbolTable)).type;
        if (summandType == BOOLEAN)
            summandType = INTEGER;

        for (var otherSummand : simple.otherSummands) {
            var otherSummandType = ((RuntimePrimitiveType) otherSummand.node.getType(symbolTable)).type;
            if (otherSummandType == BOOLEAN)
                otherSummandType = INTEGER;

            int operatorOpcode = 0;

            if (summandType == otherSummandType) {
                generate(otherSummand.node);
            } else {
                if (summandType == INTEGER && otherSummandType == REAL) {
                    methodVisitor.visitInsn(I2D);
                    summandType = REAL;
                    generate(otherSummand.node);
                } else if (summandType == REAL && otherSummandType== INTEGER) {
                    generate(otherSummand.node);
                    methodVisitor.visitInsn(I2D);
                }
            }

            switch (summandType) {
                case INTEGER -> {
                    switch (otherSummand.operator) {
                        case PLUS -> operatorOpcode = IADD;
                        case MINUS -> operatorOpcode = ISUB;
                    }
                }
                case REAL -> {
                    switch (otherSummand.operator) {
                        case PLUS -> operatorOpcode = DADD;
                        case MINUS -> operatorOpcode = DSUB;
                    }
                }
            }

            methodVisitor.visitInsn(operatorOpcode);
        }
    }

    private void generate(SummandNode summand) {
        generate(summand.factor);

        if (summand.otherFactors.size() > 0)
            throw new IllegalStateException();
    }

    private void generate(FactorNode factor) {
        if (factor instanceof ModifiablePrimaryNode) {
            generate((ModifiablePrimaryNode) factor);
        } else {
            throw new IllegalStateException();
        }
    }

    private void generate(ModifiablePrimaryNode modifiablePrimary) {
        var variableIndex = variableContext.tryGetIndexOf(modifiablePrimary, modifiablePrimary.identifier.name);
        var type = symbolTable.getType(modifiablePrimary, modifiablePrimary.identifier.name);
        int opcode = 0;

        if (type instanceof RuntimePrimitiveType) {
            opcode = switch (((RuntimePrimitiveType) type).type) {
                case INTEGER, BOOLEAN -> ILOAD;
                case REAL -> DLOAD;
            };
        } else {
            opcode = ALOAD;
        }

        methodVisitor.visitVarInsn(opcode, variableIndex);

        if (modifiablePrimary.accessors.size() > 0)
            throw new IllegalStateException();
    }
}
