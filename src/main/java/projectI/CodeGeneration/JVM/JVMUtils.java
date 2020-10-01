package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Declarations.TypeNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.AST.Types.RuntimeType;
import projectI.AST.Types.VoidRuntimeType;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class JVMUtils {
    public static void generateBuiltIns(ClassWriter classWriter) {
        generatePrint(classWriter, PrimitiveType.INTEGER, "printInt", "(I)V");
        generatePrint(classWriter, PrimitiveType.BOOLEAN, "printBoolean", "(I)V");
        generatePrint(classWriter, PrimitiveType.REAL, "printReal", "(D)V");

        MethodVisitor mainVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mainVisitor.visitMethodInsn(INVOKESTATIC, "Program", "main", "()V", false);
        mainVisitor.visitInsn(RETURN);
        mainVisitor.visitEnd();
    }

    public static void generatePrint(ClassWriter classWriter, PrimitiveType primitive, String name, String descriptor) {
        MethodVisitor methodVisitor3 = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, name, descriptor, null, null);
        methodVisitor3.visitCode();

        methodVisitor3.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        var opcode = switch (primitive) {
            case INTEGER, BOOLEAN -> ILOAD;
            case REAL -> DLOAD;
        };

        var printArg = switch (primitive) {
            case INTEGER -> "I";
            case REAL -> "D";
            case BOOLEAN -> "Z";
        };

        methodVisitor3.visitVarInsn(opcode, 0);
        methodVisitor3.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + printArg + ")V", false);
        methodVisitor3.visitInsn(RETURN);
        methodVisitor3.visitMaxs(0, 0);
        methodVisitor3.visitEnd();
    }

    public static void generateCastIfNecessary(MethodVisitor methodVisitor, RuntimeType from, RuntimeType to) {
        if (from.equals(to)) return;
        if (!(from instanceof RuntimePrimitiveType)) return;
        if (!(to instanceof RuntimePrimitiveType)) return;

        var fromPrimitiveType = ((RuntimePrimitiveType) from).type;
        if (fromPrimitiveType == PrimitiveType.BOOLEAN)
            fromPrimitiveType = PrimitiveType.INTEGER;

        var toPrimitiveType = ((RuntimePrimitiveType) to).type;
        if (toPrimitiveType == PrimitiveType.BOOLEAN)
            toPrimitiveType = PrimitiveType.INTEGER;

        if (fromPrimitiveType == PrimitiveType.INTEGER && toPrimitiveType == PrimitiveType.REAL)
            methodVisitor.visitInsn(I2D);
        else if (fromPrimitiveType == PrimitiveType.REAL && toPrimitiveType == PrimitiveType.INTEGER)
            methodVisitor.visitInsn(D2I);
    }

    public static String getJavaTypeName(RuntimeType runtimeType) {
        if (runtimeType == null || runtimeType.equals(VoidRuntimeType.instance))
            return "V";

        if (runtimeType instanceof RuntimePrimitiveType) {
            return switch (((RuntimePrimitiveType) runtimeType).type) {
                case BOOLEAN, INTEGER -> "I";
                case REAL -> "D";
            };
        } else {
            throw new IllegalStateException();
        }
    }

    public static void generateGet(MethodVisitor methodVisitor, ModifiablePrimaryNode modifiablePrimary, SymbolTable symbolTable, ProgramNode program, VariableContext variableContext) {
        var name = modifiablePrimary.identifier.name;

        if (symbolTable.isDefinedAt(program, name)) {
            methodVisitor.visitFieldInsn(GETSTATIC, "Program", name, JVMUtils.getJavaTypeName(symbolTable.getType(modifiablePrimary, name)));
        } else {
            var parameterIndex = tryGetParameterIndex(variableContext, name);
            int variableIndex;

            if (parameterIndex == -1) {
                variableIndex = variableContext.tryGetIndexOf(modifiablePrimary, name);
            } else {
                variableIndex = parameterIndex;
            }

            var type = symbolTable.getType(modifiablePrimary, name);

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
        }

        if (modifiablePrimary.accessors.size() > 0)
            throw new IllegalStateException();
    }

    private static int tryGetParameterIndex(VariableContext variableContext, String name) {
        var routine = variableContext.routine;

        List<Pair<IdentifierNode, TypeNode>> parameters = routine.parameters.parameters;

        for (int index = 0; index < parameters.size(); index++) {
            Pair<IdentifierNode, TypeNode> parameter = parameters.get(index);
            if (parameter.getValue0().name.equals(name))
                return index;
        }

        return -1;
    }

    public static void generateSet(MethodVisitor methodVisitor, ModifiablePrimaryNode modifiablePrimary, SymbolTable symbolTable, ProgramNode program, VariableContext variableContext) {
        var name = modifiablePrimary.identifier.name;

        if (symbolTable.isDefinedAt(program, name)) {
            methodVisitor.visitFieldInsn(PUTSTATIC, "Program", name, JVMUtils.getJavaTypeName(symbolTable.getType(modifiablePrimary, name)));
        } else {
            var parameterIndex = tryGetParameterIndex(variableContext, name);
            int variableIndex;

            if (parameterIndex == -1) {
                variableIndex = variableContext.tryGetIndexOf(modifiablePrimary, name);
            } else {
                variableIndex = parameterIndex;
            }

            var type = symbolTable.getType(modifiablePrimary, name);

            int opcode = 0;

            if (type instanceof RuntimePrimitiveType) {
                opcode = switch (((RuntimePrimitiveType) type).type) {
                    case INTEGER, BOOLEAN -> ISTORE;
                    case REAL -> DSTORE;
                };
            } else {
                opcode = ASTORE;
            }

            methodVisitor.visitVarInsn(opcode, variableIndex);
        }

        if (modifiablePrimary.accessors.size() > 0)
            throw new IllegalStateException();
    }

    public static void generateRoutineCall(ProgramNode program, MethodVisitor methodVisitor, RoutineCallNode routineCall, VariableContext context, SymbolTable symbolTable) {
        var routineType = (RuntimeRoutineType) symbolTable.getType(routineCall, routineCall.name.name);

        for (var index = 0; index < routineCall.arguments.size(); index++) {
            var argument = routineCall.arguments.get(index);
            var parameterType = routineType.parameters.get(index);
            var argumentType = pushExpression(program, methodVisitor, argument, context, symbolTable);
            generateCastIfNecessary(methodVisitor, argumentType, parameterType);
        }

        var descriptor = getDescriptor(routineType);
        methodVisitor.visitMethodInsn(INVOKESTATIC, JVMCodeGenerator.className, routineCall.name.name, descriptor, false);
    }

    public static RuntimeType pushExpression(ProgramNode program, MethodVisitor methodVisitor, ExpressionNode expression, VariableContext context, SymbolTable symbolTable) {
        var generator = new ExpressionCodeGenerator(program, methodVisitor, expression, context, symbolTable);
        return generator.generate();
    }

    public static String getDescriptor(RuntimeRoutineType routineType) {
        var descriptor = new StringBuilder().append('(');

        for (var parameter : routineType.parameters) {
            descriptor.append(getJavaTypeName(parameter));
        }

        descriptor.append(')');
        descriptor.append(getJavaTypeName(routineType.returnType));
        return descriptor.toString();
    }
}
