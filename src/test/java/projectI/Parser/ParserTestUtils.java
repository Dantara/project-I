package projectI.Parser;

import projectI.AST.*;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Primary.IntegralLiteralNode;
import projectI.AST.Primary.ModifiablePrimaryNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.StatementNode;

import static projectI.AST.ASTUtils.integerExpression;
import static projectI.AST.ASTUtils.booleanExpression;
import static projectI.AST.ASTUtils.toExpression;
import static projectI.AST.ASTUtils.toSimple;


public class ParserTestUtils {
    public static VariableDeclarationNode integerDeclaration(String identifier, Integer value) {
        return new VariableDeclarationNode(new IdentifierNode(identifier), new PrimitiveTypeNode(PrimitiveType.INTEGER), value == null ? null : integerExpression(value));
    }

    public static VariableDeclarationNode implicitIntegerDeclaration(String identifier, int value) {
        return new VariableDeclarationNode(new IdentifierNode(identifier), null, integerExpression(value));
    }

    public static VariableDeclarationNode implicitIntegerDeclaration(String identifier, ExpressionNode expression) {
        return new VariableDeclarationNode(new IdentifierNode(identifier), null, expression);
    }

    public static AssignmentNode integerAssignment(String identifier, Integer value) {
        return new AssignmentNode(new ModifiablePrimaryNode(new IdentifierNode(identifier)), integerExpression(value));
    }

    public static AssignmentNode integerAssignment(String identifier, ExpressionNode expression) {
        return new AssignmentNode(new ModifiablePrimaryNode(new IdentifierNode(identifier)), expression);
    }

    public static ExpressionNode integerAddition(String identifier, String otherIdentifier) {
        return toExpression(ASTUtils.toSimple(new ModifiablePrimaryNode(new IdentifierNode(identifier)))
                .addSummand(AdditionOperator.PLUS, new SummandNode(new ModifiablePrimaryNode(new IdentifierNode(otherIdentifier)))));
    }

    public static VariableDeclarationNode booleanDeclaration(String identifier, Boolean value) {
        return new VariableDeclarationNode(new IdentifierNode(identifier), new PrimitiveTypeNode(PrimitiveType.BOOLEAN), value == null ? null : booleanExpression(value));
    }

    public static ProgramNode programDeclaration(DeclarationNode[] declarations) {
        var program = new ProgramNode();

        for (DeclarationNode declaration: declarations) {
            program.addDeclaration(declaration);
        }
        return program;
    }

    public static RoutineDeclarationNode mainRoutine(StatementNode[] statements) {
        var body = new BodyNode();

        for (StatementNode statement: statements) {
            body.add(statement);
        }
        
        return new RoutineDeclarationNode(new IdentifierNode("main"), new ParametersNode(), body);
    }

    public static ProgramNode mainProgram(StatementNode[] statements) {
        return programDeclaration(new DeclarationNode[] {
            mainRoutine(statements)
        });
    }

    public static ForLoopNode forLoop(String identifier, int from, int to, StatementNode[] statements) {
        var body = new BodyNode();

        for (StatementNode statement: statements) {
            body.add(statement);
        }

        return new ForLoopNode(new IdentifierNode(identifier), new RangeNode(integerExpression(from), integerExpression(to), false), body);
    }

    public static IfStatementNode ifStatement(ExpressionNode condition, StatementNode[] statements) {
        var body = new BodyNode();

        for (StatementNode statement: statements) {
            body.add(statement);
        }

        return new IfStatementNode(condition, body);
    }

    public static SummandNode mod(String identifier, int modulo) {
        return new SummandNode(new ModifiablePrimaryNode(new IdentifierNode(identifier)))
                .addFactor(MultiplicationOperator.MODULO, new IntegralLiteralNode(modulo));
    }

    public static ExpressionNode equal(SummandNode summand, int value) {
        return new ExpressionNode(new BinaryRelationNode(new SimpleNode(summand), BinaryRelationNode.Comparison.EQUAL, toSimple(new IntegralLiteralNode(value))));
    }

    public static ExpressionNode notEqual(SummandNode summand, int value) {
        return new ExpressionNode(new BinaryRelationNode(new SimpleNode(summand), BinaryRelationNode.Comparison.NOT_EQUAL, toSimple(new IntegralLiteralNode(value))));
    }

     public static TypeDeclarationNode recordTypeDeclaration(String identifier, VariableDeclarationNode[] variables) {
         var record = new RecordTypeNode();
        
         for (VariableDeclarationNode variable: variables) {
             record.addVariable(variable);
         }

         return new TypeDeclarationNode(new IdentifierNode(identifier), record);
     }

     public static TypeDeclarationNode arrayTypeDeclaration(String identifier, int length, TypeNode type) {
         return new TypeDeclarationNode(new IdentifierNode(identifier), new ArrayTypeNode(integerExpression(length), type));
     }

     public static AssignmentNode recordMemberAssignment(String variable, String member, ExpressionNode value) {
        return new AssignmentNode(recordMember(variable, member), value);
     }

     public static ModifiablePrimaryNode recordMember(String variable, String member) {
         return new ModifiablePrimaryNode(new IdentifierNode(variable)).addMember(new IdentifierNode(member));
     }

    public static ModifiablePrimaryNode arraySize(String array) {
        return new ModifiablePrimaryNode(new IdentifierNode(array)).addArraySize();
    }

    public static ModifiablePrimaryNode arrayIndex(String array, int index) {
        return new ModifiablePrimaryNode(new IdentifierNode(array)).addIndexer(toExpression(new IntegralLiteralNode(index)));
    }

    public static VariableDeclarationNode typedVariable(String variable, String type) {
        return new VariableDeclarationNode(new IdentifierNode(variable), new IdentifierNode(type), null);
    }

    public static ExpressionNode variableValue(String variable) {
        return toExpression(new ModifiablePrimaryNode(new IdentifierNode(variable)));
    }
}
