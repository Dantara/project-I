package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Declarations.PrimitiveTypeNode;
import projectI.AST.Expressions.*;
import projectI.AST.Primary.BooleanLiteralNode;
import projectI.AST.Primary.IntegralLiteralNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Primary.RealLiteralNode;
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
            generate(expression);
        }

        return expression.getType(symbolTable);
    }

    public void generate(ExpressionNode expression) {
        generate(expression.relation);

        if (expression.otherRelations.size() == 0) return;

        var type = ((RuntimePrimitiveType) expression.relation.getType(symbolTable)).type;
        if (type == REAL) {
            methodVisitor.visitInsn(D2I);
        }

        var trueLabel = new Label();
        var exitLabel = new Label();

        methodVisitor.visitJumpInsn(IFNE, trueLabel);

        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitJumpInsn(GOTO, exitLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitLabel(exitLabel);

        for (var otherRelation : expression.otherRelations) {
            generate(otherRelation.node);

            trueLabel = new Label();
            exitLabel = new Label();

            methodVisitor.visitJumpInsn(IFNE, trueLabel);

            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitJumpInsn(GOTO, exitLabel);
            methodVisitor.visitLabel(trueLabel);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitLabel(exitLabel);

            switch (otherRelation.operator) {
                case OR -> methodVisitor.visitInsn(IOR);
                case AND -> methodVisitor.visitInsn(IAND);
                case XOR -> methodVisitor.visitInsn(IXOR);
            }
        }
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

        if (relation.otherSimple == null) return;
        if (relation.comparison == null) return;

        var type = ((RuntimePrimitiveType) relation.simple.getType(symbolTable)).type;
        if (type == BOOLEAN)
            type = INTEGER;

        var otherType = ((RuntimePrimitiveType) relation.otherSimple.getType(symbolTable)).type;
        if (otherType == BOOLEAN)
            otherType = INTEGER;

        var trueLabel = new Label();
        var exitLabel = new Label();

        if (type != otherType) {
            if (type == REAL) {
                generate(relation.otherSimple);
                methodVisitor.visitInsn(I2D);
            } else if (otherType == REAL) {
                methodVisitor.visitInsn(I2D);
                generate(relation.otherSimple);
            }
        } else {
            generate(relation.otherSimple);
        }

        if (type == REAL && otherType == REAL) {
            methodVisitor.visitInsn(DCMPL);
        }

        switch (relation.comparison) {
            case LESS -> methodVisitor.visitJumpInsn(IF_ICMPLT, trueLabel);
            case LESS_EQUAL -> methodVisitor.visitJumpInsn(IF_ICMPLE, trueLabel);
            case GREATER -> methodVisitor.visitJumpInsn(IF_ICMPGE, trueLabel);
            case GREATER_EQUAL -> methodVisitor.visitJumpInsn(IF_ICMPGT, trueLabel);
            case EQUAL -> methodVisitor.visitJumpInsn(IF_ICMPEQ, trueLabel);
            case NOT_EQUAL -> methodVisitor.visitJumpInsn(IF_ICMPNE, trueLabel);
        }

        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitJumpInsn(GOTO, exitLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitLabel(exitLabel);
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
        {
            var type = ((RuntimePrimitiveType) summand.factor.getType(symbolTable)).type;
            if (type == BOOLEAN)
                type = INTEGER;

            for (var factor : summand.otherFactors) {
                var factorType = ((RuntimePrimitiveType) factor.node.getType(symbolTable)).type;
                if (factorType == BOOLEAN)
                    factorType = INTEGER;

                if (type != factorType) {
                    if (type == INTEGER && factorType == REAL) {
                        if (factor.operator == MultiplicationOperator.MODULO) {
                            generate(factor.node);
                            methodVisitor.visitInsn(D2I);
                        } else {
                            type = REAL;
                            methodVisitor.visitInsn(I2D);
                            generate(factor.node);
                        }
                    } else if (type == REAL && factorType == INTEGER) {
                        if (factor.operator == MultiplicationOperator.MODULO) {
                            type = INTEGER;
                            methodVisitor.visitInsn(D2I);
                            generate(factor.node);
                        } else {
                            generate(factor.node);
                            methodVisitor.visitInsn(I2D);
                        }
                    }
                } else {
                    generate(factor.node);
                }

                switch (factor.operator) {
                    case MODULO -> methodVisitor.visitInsn(IREM);
                    case DIVIDE -> {
                        switch (type) {
                            case INTEGER -> methodVisitor.visitInsn(IDIV);
                            case REAL -> methodVisitor.visitInsn(DDIV);
                        }
                    }
                    case MULTIPLY -> {
                        switch (type) {
                            case INTEGER -> methodVisitor.visitInsn(IMUL);
                            case REAL -> methodVisitor.visitInsn(DMUL);
                        }
                    }
                }
            }
        }
    }

    private void generate(FactorNode factor) {
        if (factor instanceof ModifiablePrimaryNode) {
            generate((ModifiablePrimaryNode) factor);
        } else if (factor instanceof ExpressionNode) {
            generate((ExpressionNode) factor);
        } else if (factor instanceof IntegralLiteralNode) {
            var literal = (IntegralLiteralNode) factor;
            methodVisitor.visitLdcInsn(literal.value);
        } else if (factor instanceof RealLiteralNode) {
            var literal = (RealLiteralNode) factor;
            methodVisitor.visitLdcInsn(literal.value);
        } else if (factor instanceof BooleanLiteralNode) {
            var literal = (BooleanLiteralNode) factor;
            methodVisitor.visitLdcInsn(literal.value ? 1 : 0);
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
