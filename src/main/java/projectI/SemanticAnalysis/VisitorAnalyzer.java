package projectI.SemanticAnalysis;

import projectI.AST.ASTNode;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.Primary.*;
import projectI.AST.ProgramNode;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Statements.StatementNode;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class VisitorAnalyzer implements SemanticAnalyzer {
    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) program, symbolTable);

        for (var declaration : program.declarations) {
            if (declaration instanceof SimpleDeclarationNode)
                analyze((SimpleDeclarationNode) declaration, symbolTable);
            else if (declaration instanceof RoutineDeclarationNode)
                analyze((RoutineDeclarationNode) declaration, symbolTable);
        }
    }

    protected void analyze(ASTNode node, SymbolTable symbolTable) throws SemanticAnalysisException {

    }

    protected void analyze(SimpleDeclarationNode simple, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) simple, symbolTable);

        if (simple instanceof VariableDeclarationNode)
            analyze((VariableDeclarationNode) simple, symbolTable);
        else if (simple instanceof TypeDeclarationNode)
            analyze((TypeDeclarationNode) simple, symbolTable);
    }

    protected void analyze(VariableDeclarationNode variable, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) variable, symbolTable);

        analyze(variable.identifier, symbolTable);

        if (variable.type != null)
            analyze(variable.type, symbolTable);

        if (variable.expression != null)
            analyze(variable.expression, symbolTable);
    }

    protected void analyze(IdentifierNode identifier, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) identifier, symbolTable);
    }

    protected void analyze(TypeNode type, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) type, symbolTable);

        if (type instanceof PrimitiveTypeNode)
            analyze((PrimitiveTypeNode) type, symbolTable);
        else if (type instanceof ArrayTypeNode)
            analyze((ArrayTypeNode) type, symbolTable);
        else if (type instanceof RecordTypeNode)
            analyze((RecordTypeNode) type, symbolTable);
        else if (type instanceof IdentifierNode)
            analyze((IdentifierNode) type, symbolTable);
    }

    protected void analyze(PrimitiveTypeNode type, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) type, symbolTable);
    }

    protected void analyze(ArrayTypeNode array, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) array, symbolTable);

        if (array.size != null)
            analyze(array.size, symbolTable);

        analyze(array.elementType, symbolTable);
    }

    protected void analyze(RecordTypeNode type, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) type, symbolTable);

        for (var variable : type.variables) {
            analyze(variable, symbolTable);
        }
    }

    protected void analyze(ExpressionNode expression, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) expression, symbolTable);

        analyze(expression.relation, symbolTable);

        for (var relation : expression.otherRelations)
            analyze(relation.node, symbolTable);
    }

    protected void analyze(RelationNode relation, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) relation, symbolTable);

        if (relation instanceof BinaryRelationNode)
            analyze((BinaryRelationNode) relation, symbolTable);
        else if (relation instanceof NegatedRelationNode)
            analyze((NegatedRelationNode) relation, symbolTable);
    }

    protected void analyze(BinaryRelationNode relation, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) relation, symbolTable);

        analyze(relation.simple, symbolTable);

        if (relation.otherSimple != null)
            analyze(relation.otherSimple, symbolTable);
    }

    protected void analyze(NegatedRelationNode relation, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) relation, symbolTable);

        analyze(relation.innerRelation, symbolTable);
    }

    protected void analyze(SimpleNode simple, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) simple, symbolTable);

        analyze(simple.summand, symbolTable);

        for (var summand : simple.otherSummands)
            analyze(summand.node, symbolTable);
    }

    protected void analyze(SummandNode summand, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) summand, symbolTable);

        analyze(summand.factor, symbolTable);

        for (var factor : summand.otherFactors)
            analyze(factor.node, symbolTable);
    }

    protected void analyze(FactorNode factor, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) factor, symbolTable);

        if (factor instanceof PrimaryNode)
            analyze((PrimaryNode) factor, symbolTable);
        else if (factor instanceof ExpressionNode)
            analyze((ExpressionNode) factor, symbolTable);
    }

    protected void analyze(PrimaryNode primary, SymbolTable symbolTable) throws SemanticAnalysisException  {
        analyze((ASTNode) primary, symbolTable);

        if (primary instanceof IntegralLiteralNode)
            analyze((IntegralLiteralNode) primary, symbolTable);
        else if (primary instanceof RealLiteralNode)
            analyze((RealLiteralNode) primary, symbolTable);
        else if (primary instanceof BooleanLiteralNode)
            analyze((BooleanLiteralNode) primary, symbolTable);
        else if (primary instanceof ModifiablePrimaryNode)
            analyze((ModifiablePrimaryNode) primary, symbolTable);
    }

    protected void analyze(IntegralLiteralNode literal, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) literal, symbolTable);
    }

    protected void analyze(RealLiteralNode literal, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) literal, symbolTable);
    }

    protected void analyze(BooleanLiteralNode literal, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) literal, symbolTable);
    }

    protected void analyze(ModifiablePrimaryNode modifiablePrimary, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) modifiablePrimary, symbolTable);

        analyze(modifiablePrimary.identifier, symbolTable);

        for (var accessor : modifiablePrimary.accessors) {
            analyze(accessor, symbolTable);
        }
    }

    protected void analyze(ModifiablePrimaryNode.Accessor accessor, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (accessor instanceof ModifiablePrimaryNode.Member)
            analyze((ModifiablePrimaryNode.Member) accessor, symbolTable);
        else if (accessor instanceof ModifiablePrimaryNode.Indexer)
            analyze((ModifiablePrimaryNode.Indexer) accessor, symbolTable);
        else if (accessor instanceof ModifiablePrimaryNode.ArraySize)
            analyze((ModifiablePrimaryNode.ArraySize) accessor, symbolTable);
    }

    protected void analyze(ModifiablePrimaryNode.Member accessor, SymbolTable symbolTable) throws SemanticAnalysisException {

    }

    protected void analyze(ModifiablePrimaryNode.Indexer accessor, SymbolTable symbolTable) throws SemanticAnalysisException {

    }

    protected void analyze(ModifiablePrimaryNode.ArraySize accessor, SymbolTable symbolTable) throws SemanticAnalysisException {

    }

    protected void analyze(TypeDeclarationNode type, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) type, symbolTable);

        analyze(type.identifier, symbolTable);
        analyze(type.type, symbolTable);
    }

    protected void analyze(RoutineDeclarationNode routine, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) routine, symbolTable);

        analyze(routine.name, symbolTable);
        analyze(routine.parameters, symbolTable);

        if (routine.returnType != null)
            analyze(routine.returnType, symbolTable);

        analyze(routine.body, symbolTable);
    }

    protected void analyze(ParametersNode parameters, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) parameters, symbolTable);

        for (var parameter : parameters.parameters) {
            analyze(parameter.getValue0(), symbolTable);
            analyze(parameter.getValue1(), symbolTable);
        }
    }

    protected void analyze(BodyNode body, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) body, symbolTable);

        for (var statement : body.statements) {
            analyze(statement, symbolTable);
        }
    }

    protected void analyze(StatementNode statement, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) statement, symbolTable);

        if (statement instanceof AssignmentNode)
            analyze((AssignmentNode) statement, symbolTable);
        else if (statement instanceof RoutineCallNode)
            analyze((RoutineCallNode) statement, symbolTable);
        else if (statement instanceof WhileLoopNode)
            analyze((WhileLoopNode) statement, symbolTable);
        else if (statement instanceof ForLoopNode)
            analyze((ForLoopNode) statement, symbolTable);
        else if (statement instanceof IfStatementNode)
            analyze((IfStatementNode) statement, symbolTable);
        else if (statement instanceof ReturnStatementNode)
            analyze((ReturnStatementNode) statement, symbolTable);
    }

    protected void analyze(AssignmentNode assignment, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) assignment, symbolTable);

        analyze(assignment.modifiable, symbolTable);
        analyze(assignment.assignedValue, symbolTable);
    }

    protected void analyze(RoutineCallNode routineCall, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) routineCall, symbolTable);

        analyze(routineCall.name, symbolTable);

        for (var argument : routineCall.arguments) {
            analyze(argument, symbolTable);
        }
    }

    protected void analyze(WhileLoopNode whileLoop, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) whileLoop, symbolTable);

        analyze(whileLoop.condition, symbolTable);
        analyze(whileLoop.body, symbolTable);
    }

    protected void analyze(ForLoopNode forLoop, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) forLoop, symbolTable);

        analyze(forLoop.variable, symbolTable);
        analyze(forLoop.range, symbolTable);
        analyze(forLoop.body, symbolTable);
    }

    protected void analyze(IfStatementNode ifStatement, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) ifStatement, symbolTable);

        analyze(ifStatement.condition, symbolTable);
        analyze(ifStatement.body, symbolTable);

        if (ifStatement.elseBody != null)
            analyze(ifStatement.elseBody, symbolTable);
    }

    protected void analyze(ReturnStatementNode returnStatement, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) returnStatement, symbolTable);

        if (returnStatement.expression != null)
            analyze(returnStatement.expression, symbolTable);
    }

    protected void analyze(RangeNode range, SymbolTable symbolTable) throws SemanticAnalysisException {
        analyze((ASTNode) range, symbolTable);

        analyze(range.from, symbolTable);
        analyze(range.to, symbolTable);
    }
}
