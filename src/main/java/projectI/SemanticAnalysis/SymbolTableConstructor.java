package projectI.SemanticAnalysis;

import projectI.AST.Declarations.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.ProgramNode;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeRoutineType;
import projectI.AST.Types.RuntimeType;
import projectI.AST.Types.VoidRuntimeType;
import projectI.SemanticAnalysis.Exceptions.SemanticAnalysisException;

public class SymbolTableConstructor implements SemanticAnalyzer {
    @Override
    public void analyze(ProgramNode program, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var declaration : program.declarations) {
            if (declaration instanceof TypeDeclarationNode) {
                var type = (TypeDeclarationNode) declaration;
                symbolTable.defineType(program, type.identifier.name, type.type.getType(symbolTable));
            }

            if (declaration instanceof VariableDeclarationNode) {
                var variable = (VariableDeclarationNode) declaration;
                var variableType = variable.type != null ? variable.type.getType(symbolTable) : variable.expression.getType(symbolTable);
                if (variableType == null)
                    throw new SemanticAnalysisException(this, program);
                symbolTable.defineType(program, variable.identifier.name, variableType);
            }

            if (declaration instanceof RoutineDeclarationNode) {
                analyze((RoutineDeclarationNode) declaration, symbolTable);
            }
        }
    }

    private void analyze(RoutineDeclarationNode routine, SymbolTable symbolTable) throws SemanticAnalysisException {
        RuntimeType returnType = routine.returnType != null ? routine.returnType.getType(symbolTable) : VoidRuntimeType.instance;
        var routineType = new RuntimeRoutineType(returnType);

        for (var parameter : routine.parameters.parameters) {
            var parameterType = parameter.getValue1().getType(symbolTable);
            routineType.parameters.add(parameterType);
        }

        symbolTable.defineType(routine.getParent(), routine.name.name, routineType);

        for (var parameter : routine.parameters.parameters) {
            symbolTable.defineType(routine, parameter.getValue0().name, parameter.getValue1().getType(symbolTable));
        }

        analyze((HasBody) routine, symbolTable);
    }

    private void analyze(HasBody hasBody, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (int index = 0; index < hasBody.getBodiesCount(); index++) {
            analyze(hasBody.getBody(index), symbolTable);
        }
    }

    private void analyze(BodyNode body, SymbolTable symbolTable) throws SemanticAnalysisException {
        for (var statement : body.statements) {
            if (statement instanceof SimpleDeclarationNode) {
                analyze((SimpleDeclarationNode) statement, symbolTable);
            } else if (statement instanceof HasBody) {
                if (statement instanceof ForLoopNode) {
                    var loop = (ForLoopNode) statement;
                    symbolTable.defineType(statement, loop.variable.name, new RuntimePrimitiveType(PrimitiveType.INTEGER));
                }

                analyze((HasBody) statement, symbolTable);
            }
        }
    }

    private void analyze(SimpleDeclarationNode declaration, SymbolTable symbolTable) throws SemanticAnalysisException {
        if (declaration instanceof VariableDeclarationNode) {
            var variable = (VariableDeclarationNode) declaration;
            var type = variable.type != null ? variable.type.getType(symbolTable) : variable.expression.getType(symbolTable);
            symbolTable.defineType(variable.getParent(), variable.identifier.name, type);
        }
    }
}
