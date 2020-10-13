package projectI.CodeGeneration.JVM;

import org.javatuples.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import projectI.AST.Declarations.IdentifierNode;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Declarations.TypeNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Types.*;
import projectI.SemanticAnalysis.SymbolTable;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class JVMUtils {
    public static void generateBuiltIns(ClassWriter classWriter) {
        generatePrint(classWriter, PrimitiveType.INTEGER, "printInt", "(I)V");
        generatePrint(classWriter, PrimitiveType.BOOLEAN, "printBoolean", "(I)V");
        generatePrint(classWriter, PrimitiveType.REAL, "printReal", "(D)V");
        generateReadInt(classWriter);

        MethodVisitor mainVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

        generateEntryPoint(mainVisitor, System.getProperty("entryPoint"), System.getProperty("cliArgs"));

        mainVisitor.visitInsn(RETURN);
        mainVisitor.visitEnd();
    }

    public static void generateEntryPoint(MethodVisitor mainVisitor, String entryPointFunctionName, String cliArgs) {
        if (entryPointFunctionName == null || entryPointFunctionName.equals("")) {
            entryPointFunctionName = "main";
        }

        if (cliArgs != null && !cliArgs.equals("")) {
            var cliEntries = cliArgs.split(" ");

            mainVisitor.visitLdcInsn(cliEntries.length);
            mainVisitor.visitVarInsn(NEWARRAY, T_DOUBLE);
            mainVisitor.visitInsn(DUP);
    
            for (int i = 0; i < cliEntries.length; i++) {
                mainVisitor.visitLdcInsn(i);
                mainVisitor.visitLdcInsn(Double.parseDouble(cliEntries[i]));
                mainVisitor.visitInsn(DASTORE);
                mainVisitor.visitInsn(DUP);                
            }

            mainVisitor.visitMethodInsn(INVOKESTATIC, "Program", entryPointFunctionName, "([D)V", false);
        } else {
            mainVisitor.visitMethodInsn(INVOKESTATIC, "Program", entryPointFunctionName, "()V", false);
        }
    }

    public static void generateReadInt(ClassWriter classWriter) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "readInt", "()I", null, null);
        mv.visitCode();

        mv.visitTypeInsn(NEW, "java/util/Scanner");
        mv.visitInsn(DUP);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        mv.visitVarInsn(ASTORE, 0);

        var loopLabel = new Label();
        mv.visitLabel(loopLabel);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "hasNextInt", "()Z", false);
        mv.visitJumpInsn(IFEQ, loopLabel);


        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
        mv.visitVarInsn(ISTORE, 1);

        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IRETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    public static void generatePrint(ClassWriter classWriter, PrimitiveType primitive, String name, String descriptor) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, name, descriptor, null, null);
        mv.visitCode();

        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        var opcode = switch (primitive) {
            case INTEGER, BOOLEAN -> ILOAD;
            case REAL -> DLOAD;
        };

        var printArg = switch (primitive) {
            case INTEGER -> "I";
            case REAL -> "D";
            case BOOLEAN -> "Z";
        };

        mv.visitVarInsn(opcode, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + printArg + ")V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
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

    public static String getJavaTypeName(RuntimeType runtimeType, JVMCodeGenerator codeGenerator) {
        if (runtimeType == null || runtimeType.equals(VoidRuntimeType.instance))
            return "V";

        if (runtimeType instanceof RuntimePrimitiveType) {
            return switch (((RuntimePrimitiveType) runtimeType).type) {
                case BOOLEAN, INTEGER -> "I";
                case REAL -> "D";
            };
        } else if (runtimeType instanceof RuntimeArrayType) {
            return "[" + getJavaTypeName(((RuntimeArrayType) runtimeType).elementType, codeGenerator);
        } else if (runtimeType instanceof RuntimeRecordType){
            return "L" + codeGenerator.recordClassNames.get(runtimeType) + ";";
        } else {
            throw new IllegalStateException();
        }
    }

    public static void generateGet(MethodVisitor methodVisitor, ModifiablePrimaryNode modifiablePrimary, VariableContext variableContext, JVMCodeGenerator codeGenerator) {
        generateGet(methodVisitor, modifiablePrimary, variableContext, codeGenerator, modifiablePrimary.accessors.size());
    }

    public static void generateGet(MethodVisitor methodVisitor, ModifiablePrimaryNode modifiablePrimary, VariableContext variableContext, JVMCodeGenerator codeGenerator, int accessors) {
        var name = modifiablePrimary.identifier.name;

        if (codeGenerator.symbolTable.isDefinedAt(codeGenerator.program, name)) {
            methodVisitor.visitFieldInsn(GETSTATIC, "Program", name, getJavaTypeName(codeGenerator.symbolTable.getType(modifiablePrimary, name), codeGenerator));
        } else {
            var parameterIndex = tryGetParameterIndex(variableContext, name);
            int variableIndex;

            if (parameterIndex == -1) {
                variableIndex = variableContext.tryGetIndexOf(modifiablePrimary, name);
            } else {
                variableIndex = parameterIndex;
            }

            var type = codeGenerator.symbolTable.getType(modifiablePrimary, name);

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

        var type = codeGenerator.symbolTable.getType(modifiablePrimary, modifiablePrimary.identifier.name);

        for (var index = 0; index < accessors && index < modifiablePrimary.accessors.size(); index++) {
            var accessor = modifiablePrimary.accessors.get(index);

            if (accessor instanceof ModifiablePrimaryNode.Member && type instanceof RuntimeRecordType) {
                var memberAccessor = (ModifiablePrimaryNode.Member) accessor;
                var outerRecordTypeName = codeGenerator.recordClassNames.get(type);
                var fieldType = memberAccessor.getRuntimeType(type, codeGenerator.symbolTable);
                var descriptor = getJavaTypeName(fieldType, codeGenerator);
                methodVisitor.visitFieldInsn(GETFIELD, outerRecordTypeName, memberAccessor.name.name, descriptor);
                type = fieldType;
            } else if (accessor instanceof ModifiablePrimaryNode.ArraySize && type instanceof RuntimeArrayType) {
                methodVisitor.visitInsn(ARRAYLENGTH);
                type = new RuntimePrimitiveType(PrimitiveType.INTEGER);
            } else if (accessor instanceof ModifiablePrimaryNode.Indexer && type instanceof RuntimeArrayType) {
                var indexAccessor = (ModifiablePrimaryNode.Indexer) accessor;
                var arrayType = (RuntimeArrayType) type;

                pushExpression(codeGenerator.program, methodVisitor, indexAccessor.value, variableContext, codeGenerator.symbolTable, codeGenerator);
                generateCastIfNecessary(methodVisitor, indexAccessor.value.getType(codeGenerator.symbolTable), new RuntimePrimitiveType(PrimitiveType.INTEGER));
                methodVisitor.visitInsn(ICONST_1);
                methodVisitor.visitInsn(ISUB);

                int opcode;
                if (arrayType.elementType instanceof RuntimePrimitiveType) {
                    opcode = switch (((RuntimePrimitiveType) arrayType.elementType).type) {
                        case INTEGER, BOOLEAN -> IALOAD;
                        case REAL -> DALOAD;
                    };
                } else {
                    opcode = AALOAD;
                }

                methodVisitor.visitInsn(opcode);
                type = arrayType.elementType;
            }
        }
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

    public static void generateSet(MethodVisitor methodVisitor, ModifiablePrimaryNode modifiablePrimary, ExpressionNode expression, VariableContext variableContext, JVMCodeGenerator codeGenerator) {
        var name = modifiablePrimary.identifier.name;
        var symbolTable = codeGenerator.symbolTable;
        var program = codeGenerator.program;

        if (modifiablePrimary.accessors.size() == 0) {
            var expressionType = pushExpression(program, methodVisitor, expression, variableContext, symbolTable, codeGenerator);
            var variableType = modifiablePrimary.getType(symbolTable);
            // cast it to the type of the variable
            generateCastIfNecessary(methodVisitor, expressionType, variableType);

            if (symbolTable.isDefinedAt(program, name)) {
                methodVisitor.visitFieldInsn(PUTSTATIC, "Program", name, JVMUtils.getJavaTypeName(symbolTable.getType(modifiablePrimary, name), codeGenerator));
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
        } else {
            var accessors = modifiablePrimary.accessors.size() - 1;
            generateGet(methodVisitor, modifiablePrimary, variableContext, codeGenerator, accessors);

            var preLastType = modifiablePrimary.identifier.getType(symbolTable);

            for (var index = 0; index < accessors; index++) {
                preLastType = modifiablePrimary.accessors.get(index).getRuntimeType(preLastType, symbolTable);
            }

            var variableType = modifiablePrimary.getType(symbolTable);
            var lastAccessor = modifiablePrimary.accessors.get(accessors);

            if (lastAccessor instanceof ModifiablePrimaryNode.Member) {
                var lastMemberAccessor = (ModifiablePrimaryNode.Member) lastAccessor;
                var recordName = codeGenerator.recordClassNames.get(preLastType);
                var descriptor = getJavaTypeName(variableType, codeGenerator);
                // evaluate assigned expression
                var expressionType = pushExpression(program, methodVisitor, expression, variableContext, symbolTable, codeGenerator);
                // cast it to the type of the variable
                generateCastIfNecessary(methodVisitor, expressionType, variableType);
                methodVisitor.visitFieldInsn(PUTFIELD, recordName, lastMemberAccessor.name.name, descriptor);
            } else if (lastAccessor instanceof ModifiablePrimaryNode.Indexer) {
                var lastIndexAccessor = (ModifiablePrimaryNode.Indexer) lastAccessor;
                // push index
                pushExpression(program, methodVisitor, lastIndexAccessor.value, variableContext, symbolTable, codeGenerator);
                generateCastIfNecessary(methodVisitor, lastIndexAccessor.value.getType(symbolTable), new RuntimePrimitiveType(PrimitiveType.INTEGER));
                methodVisitor.visitInsn(ICONST_1);
                methodVisitor.visitInsn(ISUB);
                // evaluate assigned expression
                var expressionType = pushExpression(program, methodVisitor, expression, variableContext, symbolTable, codeGenerator);
                // cast it to the type of the variable
                generateCastIfNecessary(methodVisitor, expressionType, variableType);

                // determine the element type
                var arrayType = (RuntimeArrayType) preLastType;
                var elementType = arrayType.elementType;
                int opcode;
                if (elementType instanceof RuntimePrimitiveType) {
                    opcode = switch (((RuntimePrimitiveType) elementType).type) {
                        case INTEGER, BOOLEAN -> IASTORE;
                        case REAL -> DASTORE;
                    };
                } else {
                    opcode = AASTORE;
                }
                methodVisitor.visitInsn(opcode);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public static void generateRoutineCall(ProgramNode program, MethodVisitor methodVisitor, RoutineCallNode routineCall, VariableContext context, SymbolTable symbolTable, JVMCodeGenerator codeGenerator) {
        var routineType = (RuntimeRoutineType) symbolTable.getType(routineCall, routineCall.name.name);

        for (var index = 0; index < routineCall.arguments.size(); index++) {
            var argument = routineCall.arguments.get(index);
            var parameterType = routineType.parameters.get(index);
            var argumentType = pushExpression(program, methodVisitor, argument, context, symbolTable, codeGenerator);
            generateCastIfNecessary(methodVisitor, argumentType, parameterType);
        }

        var descriptor = getDescriptor(routineType, codeGenerator);
        methodVisitor.visitMethodInsn(INVOKESTATIC, JVMCodeGenerator.className, routineCall.name.name, descriptor, false);
    }

    public static RuntimeType pushExpression(ProgramNode program, MethodVisitor methodVisitor, ExpressionNode expression, VariableContext context, SymbolTable symbolTable, JVMCodeGenerator codeGenerator) {
        var generator = new ExpressionCodeGenerator(methodVisitor, expression, context, codeGenerator);
        return generator.generate();
    }

    public static String getDescriptor(RuntimeRoutineType routineType, JVMCodeGenerator codeGenerator) {
        var descriptor = new StringBuilder().append('(');

        for (var parameter : routineType.parameters) {
            descriptor.append(getJavaTypeName(parameter, codeGenerator));
        }

        descriptor.append(')');
        descriptor.append(getJavaTypeName(routineType.returnType, codeGenerator));
        return descriptor.toString();
    }

    public static void generateDefaultInitialization(MethodVisitor methodVisitor, RuntimeType type, JVMCodeGenerator codeGenerator) {
        if (type instanceof RuntimePrimitiveType) {
            switch (((RuntimePrimitiveType) type).type) {
                case INTEGER, BOOLEAN -> methodVisitor.visitInsn(ICONST_0);
                case REAL -> methodVisitor.visitInsn(DCONST_0);
            }
        } else if (type instanceof RuntimeRecordType) {
            var recordClassName = codeGenerator.recordClassNames.get(type);
            methodVisitor.visitTypeInsn(NEW, recordClassName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, recordClassName, "<init>", "()V", false);
        } else if (type instanceof RuntimeArrayType) {
            var arrayType = (RuntimeArrayType) type;
            var elementType = arrayType.elementType;
            methodVisitor.visitLdcInsn(arrayType.size);
            var elementTypeName = getJavaTypeName(elementType, codeGenerator);

            if (elementType instanceof RuntimePrimitiveType) {
                var opcode = switch (((RuntimePrimitiveType) elementType).type) {
                    case INTEGER, BOOLEAN -> T_INT;
                    case REAL -> T_DOUBLE;
                };
                methodVisitor.visitIntInsn(NEWARRAY, opcode);
            } else {
                methodVisitor.visitTypeInsn(ANEWARRAY, elementTypeName);
            }

            if (elementType instanceof RuntimePrimitiveType)
                return;

            for (var i = 0; i < arrayType.size; i++) {
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitLdcInsn(i);
                generateDefaultInitialization(methodVisitor, elementType, codeGenerator);
                methodVisitor.visitInsn(AASTORE);
            }
        } else {
            throw new IllegalStateException();
        }
    }
}
