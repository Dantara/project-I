package projectI.CodeGeneration.JVM;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Declarations.RoutineDeclarationNode;
import projectI.AST.Declarations.VariableDeclarationNode;
import projectI.AST.Expressions.ExpressionNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Statements.StatementNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.AST.Types.RuntimeType;
import projectI.CodeGeneration.ICodeGenerator;
import projectI.SemanticAnalysis.SymbolTable;

import static org.objectweb.asm.Opcodes.*;
import static projectI.CodeGeneration.JVM.JVMUtils.generateCastIfNecessary;

public class JVMCodeGenerator implements ICodeGenerator {
    public static final String className = "Program";
    private final ProgramNode program;
    private final SymbolTable symbolTable;

    public JVMCodeGenerator(ProgramNode program, SymbolTable symbolTable) {
        this.program = program;
        this.symbolTable = symbolTable;
    }

    @Override
    public byte[] generate() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V1_7, ACC_PUBLIC, className, null, "java/lang/Object", null);

        generateBuiltIns(classWriter);

        for (var declaration : program.declarations) {
            if (declaration instanceof RoutineDeclarationNode)
                generateMethod(classWriter, (RoutineDeclarationNode) declaration);
        }

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void generateBuiltIns(ClassWriter classWriter) {
        MethodVisitor methodVisitor1 = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "printInt", "(I)V", null, null);
        methodVisitor1.visitCode();

        methodVisitor1.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor1.visitVarInsn(Opcodes.ILOAD, 0);
        methodVisitor1.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        methodVisitor1.visitInsn(RETURN);

        methodVisitor1.visitMaxs(0, 0);
        methodVisitor1.visitEnd();

        MethodVisitor methodVisitor2 = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "printBoolean", "(I)V", null, null);
        methodVisitor2.visitCode();

        methodVisitor2.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor2.visitVarInsn(ILOAD, 0);
        methodVisitor2.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
        methodVisitor2.visitInsn(RETURN);

        methodVisitor2.visitMaxs(0, 0);
        methodVisitor2.visitEnd();

        MethodVisitor methodVisitor3 = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "printReal", "(D)V", null, null);
        methodVisitor3.visitCode();

        methodVisitor3.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor3.visitVarInsn(DLOAD, 0);
        methodVisitor3.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
        methodVisitor3.visitInsn(RETURN);

        methodVisitor3.visitMaxs(0, 0);
        methodVisitor3.visitEnd();
    }

    private void generateMethod(ClassWriter classWriter, RoutineDeclarationNode routine) {
        var context = new VariableContext();
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, routine.name.name, "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();

        for (var statement : routine.body.statements)
            generate(methodVisitor, routine, statement, context);

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private void generate(MethodVisitor methodVisitor, RoutineDeclarationNode routine, StatementNode statement, VariableContext context) {
        if (statement instanceof ReturnStatementNode)
            generate(methodVisitor, (ReturnStatementNode) statement, context);
        else if (statement instanceof VariableDeclarationNode)
            generateLocalVariableDeclaration(methodVisitor, routine, (VariableDeclarationNode) statement, context);
        else if (statement instanceof AssignmentNode)
            generateAssignment(methodVisitor, (AssignmentNode) statement, context);
        else if (statement instanceof RoutineCallNode)
            generateRoutineCall(methodVisitor, (RoutineCallNode) statement, context);
        else if (statement instanceof IfStatementNode)
            generateIfStatement(methodVisitor, (IfStatementNode) statement, context);
        else
            throw new IllegalStateException();
    }

    private void generate(MethodVisitor methodVisitor, ReturnStatementNode returnStatement, VariableContext context) {
        if (returnStatement.expression == null) {
            methodVisitor.visitInsn(RETURN);
        } else {
            var expressionType = pushExpression(methodVisitor, returnStatement.expression, context);

            if (expressionType instanceof RuntimePrimitiveType) {
                var primitiveType = (RuntimePrimitiveType) expressionType;

                switch (primitiveType.type) {
                    case INTEGER, BOOLEAN -> methodVisitor.visitInsn(IRETURN);
                    case REAL -> methodVisitor.visitInsn(DRETURN);
                }
            } else {
                methodVisitor.visitInsn(ARETURN);
            }
        }
    }

    private RuntimeType pushExpression(MethodVisitor methodVisitor, ExpressionNode expression, VariableContext context) {
        var generator = new ExpressionCodeGenerator(methodVisitor, expression, context, symbolTable);
        return generator.generate();
    }

    private void generateLocalVariableDeclaration(MethodVisitor methodVisitor, RoutineDeclarationNode routine, VariableDeclarationNode variableDeclaration, VariableContext context) {
        var id = context.defineVariable(routine, variableDeclaration.getParent(), variableDeclaration.identifier.name);
        generateLocalVariableInitialization(methodVisitor, context, variableDeclaration, id);
    }

    private void generateLocalVariableInitialization(MethodVisitor methodVisitor, VariableContext context, VariableDeclarationNode variableDeclaration, int variableId) {
        if (variableDeclaration.expression == null) {
            generateLocalVariableDefaultInitialization(methodVisitor, variableDeclaration, variableId);
        } else {
            var expressionType = pushExpression(methodVisitor, variableDeclaration.expression, context);

            if (variableDeclaration.type != null)
                generateCastIfNecessary(methodVisitor, expressionType, variableDeclaration.type.getType(symbolTable));

            storeVariable(methodVisitor, variableDeclaration.getType(symbolTable), variableId);
        }
    }

    private void generateLocalVariableDefaultInitialization(MethodVisitor methodVisitor, VariableDeclarationNode variable, int variableId) {
        var type = variable.getType(symbolTable);
        if (type instanceof RuntimePrimitiveType) {
            switch (((RuntimePrimitiveType) type).type) {
                case INTEGER, BOOLEAN -> methodVisitor.visitInsn(ICONST_0);
                case REAL -> methodVisitor.visitInsn(DCONST_0);
            }

            storeVariable(methodVisitor, type, variableId);
        } else {
            throw new IllegalStateException();
        }
    }

    private void storeVariable(MethodVisitor methodVisitor, RuntimeType variableType, int variableId) {
        if (variableType instanceof RuntimePrimitiveType) {
            switch (((RuntimePrimitiveType) variableType).type) {
                case INTEGER, BOOLEAN -> methodVisitor.visitVarInsn(ISTORE, variableId);
                case REAL -> methodVisitor.visitVarInsn(DSTORE, variableId);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    private void generateAssignment(MethodVisitor methodVisitor, AssignmentNode assignment, VariableContext context) {
        if (assignment.modifiable.accessors.size() > 0) {
            throw new IllegalStateException();
        } else {
            var expressionType = pushExpression(methodVisitor, assignment.assignedValue, context);
            var variableType = assignment.modifiable.identifier.getType(symbolTable);
            generateCastIfNecessary(methodVisitor, expressionType, variableType);

            var variableName = assignment.modifiable.identifier.name;
            var variableIndex = context.tryGetIndexOf(assignment, variableName);
            storeVariable(methodVisitor, variableType, variableIndex);
        }
    }

    private void generateRoutineCall(MethodVisitor methodVisitor, RoutineCallNode routineCall, VariableContext context) {
        var routineType = (RuntimeRoutineType) symbolTable.getType(routineCall, routineCall.name.name);

        for (var index = 0; index < routineCall.arguments.size(); index++) {
            var argument = routineCall.arguments.get(index);
            var parameterType = routineType.parameters.get(index);
            var argumentType = pushExpression(methodVisitor, argument, context);
            generateCastIfNecessary(methodVisitor, argumentType, parameterType);
        }

        var descriptor = new StringBuilder().append('(');

        for (var parameter : routineType.parameters) {
            descriptor.append(getJavaTypeName(parameter));
        }

        descriptor.append(')');
        descriptor.append(getJavaTypeName(routineType.returnType));

        methodVisitor.visitMethodInsn(INVOKESTATIC, className, routineCall.name.name, descriptor.toString(), false);
    }

    private void generateIfStatement(MethodVisitor methodVisitor, IfStatementNode ifStatement, VariableContext context) {

    }

    private String getJavaTypeName(RuntimeType runtimeType) {
        if (runtimeType == null)
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
}

