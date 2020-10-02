package projectI.CodeGeneration.JVM;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import projectI.AST.Declarations.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Flow.WhileLoopNode;
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
import static projectI.CodeGeneration.JVM.JVMUtils.*;

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
        // define a class
        classWriter.visit(V1_7, ACC_PUBLIC, className, null, "java/lang/Object", null);
        // generate built-in functions
        generateBuiltIns(classWriter);

        // generate code for declarations
        for (var declaration : program.declarations) {
            if (declaration instanceof RoutineDeclarationNode)
                generateMethod(classWriter, (RoutineDeclarationNode) declaration);
            else if (declaration instanceof VariableDeclarationNode)
                generateField(classWriter, (VariableDeclarationNode) declaration);
        }

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void generateMethod(ClassWriter classWriter, RoutineDeclarationNode routine) {
        var context = new VariableContext(routine);
        var descriptor = getDescriptor((RuntimeRoutineType) symbolTable.getType(routine, routine.name.name));
        // generate method
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, routine.name.name, descriptor, null, null);
        methodVisitor.visitCode();

        // generate code for method's body
        generateBody(methodVisitor, routine.body, routine, context);

        // return in case there is no other returns in the method's code
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private void generateBody(MethodVisitor methodVisitor, BodyNode body, RoutineDeclarationNode routine, VariableContext context) {
        for (var statement : body.statements)
            generate(methodVisitor, statement, routine, context);
    }

    private void generateField(ClassWriter classWriter, VariableDeclarationNode variable) {
        var value = variable.expression.tryEvaluateConstant(symbolTable);
        var type = variable.type != null  ? variable.type.getType(symbolTable) : variable.expression.getType(symbolTable);
        var typeName = getJavaTypeName(type);
        // create a static field
        classWriter.visitField(ACC_STATIC + ACC_PUBLIC, variable.identifier.name, typeName, null, value);
    }

    private void generate(MethodVisitor methodVisitor, StatementNode statement, RoutineDeclarationNode routine, VariableContext context) {
        if (statement instanceof ReturnStatementNode)
            generate(methodVisitor, (ReturnStatementNode) statement, context);
        else if (statement instanceof VariableDeclarationNode)
            generateLocalVariableDeclaration(methodVisitor, (VariableDeclarationNode) statement, context);
        else if (statement instanceof AssignmentNode)
            generateAssignment(methodVisitor, (AssignmentNode) statement, context);
        else if (statement instanceof RoutineCallNode)
            generateRoutineCall(program, methodVisitor, (RoutineCallNode) statement, context, symbolTable);
        else if (statement instanceof IfStatementNode)
            generateIfStatement(methodVisitor, (IfStatementNode) statement, routine, context);
        else if (statement instanceof ForLoopNode)
            generateForLoop(methodVisitor, (ForLoopNode) statement, routine, context);
        else if (statement instanceof WhileLoopNode)
            generateWhileLoop(methodVisitor, (WhileLoopNode) statement, routine, context);
        else
            throw new IllegalStateException();
    }

    private void generate(MethodVisitor methodVisitor, ReturnStatementNode returnStatement, VariableContext context) {
        if (returnStatement.expression == null) {
            methodVisitor.visitInsn(RETURN);
        } else {
            var expressionType = pushExpression(program, methodVisitor, returnStatement.expression, context, symbolTable);

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

    private void generateLocalVariableDeclaration(MethodVisitor methodVisitor, VariableDeclarationNode variableDeclaration, VariableContext context) {
        var id = context.defineVariable(variableDeclaration.getParent(), variableDeclaration.identifier.name);
        generateLocalVariableInitialization(methodVisitor, context, variableDeclaration, id);
    }

    private void generateLocalVariableInitialization(MethodVisitor methodVisitor, VariableContext context, VariableDeclarationNode variableDeclaration, int variableId) {
        if (variableDeclaration.expression == null) {
            generateLocalVariableDefaultInitialization(methodVisitor, variableDeclaration, variableId);
        } else {
            var expressionType = pushExpression(program, methodVisitor, variableDeclaration.expression, context, symbolTable);

            if (variableDeclaration.type != null)
                generateCastIfNecessary(methodVisitor, expressionType, variableDeclaration.type.getType(symbolTable));

            storeVariable(methodVisitor, variableDeclaration.getType(symbolTable), variableId);
        }
    }

    private void generateLocalVariableDefaultInitialization(MethodVisitor methodVisitor, VariableDeclarationNode variable, int variableId) {
        var type = variable.getType(symbolTable);
        generateLocalVariableDefaultInitialization(methodVisitor, type, variableId);
    }

    private void generateLocalVariableDefaultInitialization(MethodVisitor methodVisitor, RuntimeType type, int variableId) {
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
            // evaluate assigned expression
            var expressionType = pushExpression(program, methodVisitor, assignment.assignedValue, context, symbolTable);
            var variableType = assignment.modifiable.identifier.getType(symbolTable);
            // cast it to the type of the variable
            generateCastIfNecessary(methodVisitor, expressionType, variableType);

            // assign the value
            generateSet(methodVisitor, assignment.modifiable, symbolTable, program, context);
        }
    }

    private void generateIfStatement(MethodVisitor methodVisitor, IfStatementNode ifStatement, RoutineDeclarationNode routine, VariableContext context) {
        // evaluate the condition
        var conditionType = pushExpression(program, methodVisitor, ifStatement.condition, context, symbolTable);
        // cast it to boolean
        generateCastIfNecessary(methodVisitor, conditionType, new RuntimePrimitiveType(PrimitiveType.BOOLEAN));

        if (ifStatement.elseBody == null) { // if there is not else
            var exitLabel = new Label();
            // exit if condition is not met
            methodVisitor.visitJumpInsn(IFEQ, exitLabel);
            // otherwise, if condition is met, execute the body
            generateBody(methodVisitor, ifStatement.body, routine, context);
            methodVisitor.visitLabel(exitLabel);
        } else {
            var elseLabel = new Label();
            var exitLabel = new Label();

            // if false, execute else body
            methodVisitor.visitJumpInsn(IFEQ, elseLabel);
            // if true, execute the body
            generateBody(methodVisitor, ifStatement.body, routine, context);
            // exit
            methodVisitor.visitJumpInsn(GOTO, exitLabel);

            methodVisitor.visitLabel(elseLabel);
            generateBody(methodVisitor, ifStatement.elseBody, routine, context);

            methodVisitor.visitLabel(exitLabel);
        }
    }

    private void generateForLoop(MethodVisitor methodVisitor, ForLoopNode forLoop, RoutineDeclarationNode routine, VariableContext context) {
        // define the iterator variable
        var variableId = context.defineVariable(forLoop, forLoop.variable.name);
        generateLocalVariableDefaultInitialization(methodVisitor, new RuntimePrimitiveType(PrimitiveType.INTEGER), variableId);

        var exitLabel = new Label();
        var loopLabel = new Label();

        var range = forLoop.range;
        var initialValue = range.reverse ? range.to : range.from;
        var finalValue = range.reverse ? range.from : range.to;
        // evaluate initial value
        pushExpression(program, methodVisitor, initialValue, context, symbolTable);
        // store the initial value in the iterator
        storeVariable(methodVisitor, new RuntimePrimitiveType(PrimitiveType.INTEGER), variableId);

        methodVisitor.visitLabel(loopLabel);

        // check range
        methodVisitor.visitVarInsn(ILOAD, variableId);
        pushExpression(program, methodVisitor, finalValue, context, symbolTable);
        var exitOpcode = range.reverse ? IF_ICMPLT : IF_ICMPGT;
        // if iterator is outside the range, exit
        methodVisitor.visitJumpInsn(exitOpcode, exitLabel);

        // otherwise, execute loop body
        generateBody(methodVisitor, forLoop.body, routine, context);

        methodVisitor.visitIincInsn(variableId, range.reverse ? -1 : 1);
        methodVisitor.visitJumpInsn(GOTO, loopLabel);

        methodVisitor.visitLabel(exitLabel);
    }

    private void generateWhileLoop(MethodVisitor methodVisitor, WhileLoopNode whileLoop, RoutineDeclarationNode routine, VariableContext context) {
        var exitLabel = new Label();
        var loopLabel = new Label();

        // check condition
        methodVisitor.visitLabel(loopLabel);
        pushExpression(program, methodVisitor, whileLoop.condition, context, symbolTable);
        generateCastIfNecessary(methodVisitor, whileLoop.condition.getType(symbolTable), new RuntimePrimitiveType(PrimitiveType.BOOLEAN));
        // if false, exit
        methodVisitor.visitJumpInsn(IFEQ, exitLabel);

        // otherwise, generate body
        generateBody(methodVisitor, whileLoop.body, routine, context);
        methodVisitor.visitJumpInsn(GOTO, loopLabel);

        methodVisitor.visitLabel(exitLabel);
    }
}

