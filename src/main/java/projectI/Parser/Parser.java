package projectI.Parser;

import org.javatuples.Pair;
import projectI.AST.*;
import projectI.AST.Declarations.*;
import projectI.AST.Expressions.*;
import projectI.AST.Flow.ForLoopNode;
import projectI.AST.Flow.IfStatementNode;
import projectI.AST.Flow.RangeNode;
import projectI.AST.Flow.WhileLoopNode;
import projectI.AST.Primary.*;
import projectI.AST.Statements.AssignmentNode;
import projectI.AST.Statements.ReturnStatementNode;
import projectI.AST.Statements.RoutineCallNode;
import projectI.AST.Statements.StatementNode;
import projectI.Lexer.StringWithLocation;
import projectI.Lexer.Token;
import projectI.Lexer.TokenType;

public class Parser {
    public ProgramNode tryParseProgram() {
        return tryParseProgram(0, tokens.length);
    }

    private ProgramNode tryParseProgram(int begin, int endExclusive) {
        var program = new ProgramNode();
        int left = begin;

        while (left < endExclusive) {
            if (tokens[left].getType() == TokenType.DeclarationSeparator) {
                left++;
                continue;
            }

            DeclarationNode declaration = null;

            for (int rightExclusive = left + 1; rightExclusive <= endExclusive; rightExclusive++) {
                if (rightExclusive != endExclusive && tokens[rightExclusive].getType() != TokenType.DeclarationSeparator) continue;
                declaration = tryParseDeclaration(left, rightExclusive);

                if (declaration != null) {
                    left = rightExclusive;
                    break;
                }
            }

            if (declaration == null) return null;

            program.declarations.add(declaration);

            if (left == endExclusive) break;
            if (tokens[left].getType() != TokenType.DeclarationSeparator) return null;
        }

        return program;
    }

    private DeclarationNode tryParseDeclaration(int begin, int endExclusive) {
        var simple = tryParseSimpleDeclaration(begin, endExclusive);
        if (simple != null) return simple;

        return tryParseRoutineDeclaration(begin, endExclusive);
    }

    private SimpleDeclarationNode tryParseSimpleDeclaration(int begin, int endExclusive) {
        var variable = tryParseVariableDeclaration(begin, endExclusive);
        if (variable != null) return variable;

        return tryParseTypeDeclaration(begin, endExclusive);
    }

    public VariableDeclarationNode tryParseVariableDeclaration(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "var")) return null;

        var position = locations[begin].getPosition();

        if (begin + 1 >= endExclusive) return null;
        if (tokens[begin + 1].getType() != TokenType.Identifier) return null;

        var identifier = tryParseIdentifier(begin + 1, begin + 2);
        if (identifier == null) return null;

        if (begin + 2 >= endExclusive) return null;

        if (tokens[begin + 2].equals(TokenType.Operator, ":")) {
            var isIndex = getIndexOfLastToken(begin + 3, endExclusive, TokenType.Keyword, "is");

            if (isIndex == -1) {
                var type = tryParseType(begin + 3, endExclusive);
                if (type == null) return null;

                return new VariableDeclarationNode(identifier, type, null, position);
            } else {
                var type = tryParseType(begin + 3, isIndex);
                if (type == null) return null;
                var expression = tryParseExpression(isIndex + 1, endExclusive);
                if (expression == null) return null;

                return new VariableDeclarationNode(identifier, type, expression, position);
            }

        } else if (tokens[begin + 2].equals(TokenType.Keyword, "is")) {
            var expression = tryParseExpression(begin + 3, endExclusive);
            if (expression == null) return null;

            return new VariableDeclarationNode(identifier, null, expression, position);
        }

        return null;
    }

    private int getIndexOfLastToken(int begin, int endExclusive, TokenType type, String lexeme) {
        for (int index = endExclusive - 1; index >= begin; index--) {
            if (tokens[index].equals(type, lexeme))
                return index;
        }

        return -1;
    }

    public ExpressionNode tryParseExpression(int begin, int endExclusive) {
        int left = begin;
        RelationNode relation = null;

        for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
            relation = tryParseRelation(left, rightExclusive);

            if (relation != null) {
                left = rightExclusive;
                break;
            }
        }

        if (relation == null) return null;

        var expression = new ExpressionNode(relation);

        while (left < endExclusive) {
            var operator = tryParseLogicalOperator(tokens[left]);
            if (operator == null) return null;

            var operatorIndex = left;
            left++;

            for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
                relation = tryParseRelation(left, rightExclusive);

                if (relation != null) {
                    left = rightExclusive;
                    break;
                }
            }

            if (relation == null) return null;

            expression.addRelation(operator, relation, locations[operatorIndex].getPosition());
        }

        return expression;
    }

    private LogicalOperator tryParseLogicalOperator(Token token) {
        if (token.getType() != TokenType.Operator) return null;

        return switch (token.getLexeme()) {
            case "and" -> LogicalOperator.AND;
            case "or" -> LogicalOperator.OR;
            case "xor" -> LogicalOperator.XOR;
            default -> null;
        };
    }

    public RelationNode tryParseRelation(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (tokens[begin].equals(TokenType.Operator, "not")) {
            var innerRelation = tryParseRelation(begin + 1, endExclusive);
            if (innerRelation == null) return null;

            return new NegatedRelationNode(innerRelation, locations[begin].getPosition());
        }

        int left = begin;
        SimpleNode simple = null;

        for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
            simple = tryParseSimple(left, rightExclusive);

            if (simple != null) {
                left = rightExclusive;
                break;
            }
        }

        if (simple == null) return null;
        if (left >= endExclusive) return new BinaryRelationNode(simple);

        var comparison = tryParseComparison(tokens[left]);
        if (comparison == null) return null;

        if (left + 1 >= endExclusive) return null;

        var otherSimple = tryParseSimple(left + 1, endExclusive);
        if (otherSimple == null) return null;

        return new BinaryRelationNode(simple, comparison, otherSimple, locations[left].getPosition());
    }

    public SimpleNode tryParseSimple(int begin, int endExclusive) {
        int left = begin;
        SummandNode factor = null;

        for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
            factor = tryParseSummand(left, rightExclusive);

            if (factor != null) {
                left = rightExclusive;
                break;
            }
        }

        if (factor == null) return null;

        var simple = new SimpleNode(factor);

        while (left < endExclusive) {
            var operator = tryParseSimpleNodeOperator(tokens[left]);
            if (operator == null) return null;

            var operatorIndex = left;
            left++;

            for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
                factor = tryParseSummand(left, rightExclusive);

                if (factor != null) {
                    left = rightExclusive;
                    break;
                }
            }

            if (factor == null) return null;

            simple.addSummand(operator, factor, locations[operatorIndex].getPosition());
        }

        return simple;
    }

    public SummandNode tryParseSummand(int begin, int endExclusive) {
        int left = begin;
        FactorNode factor = null;

        for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
            factor = tryParseFactor(left, rightExclusive);

            if (factor != null) {
                left = rightExclusive;
                break;
            }
        }

        if (factor == null) return null;

        var summand = new SummandNode(factor);

        while (left < endExclusive) {
            var operator = tryParseFactorOperator(tokens[left]);
            if (operator == null) return null;

            var operatorIndex = left;
            left++;

            for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
                factor = tryParseFactor(left, rightExclusive);

                if (factor != null) {
                    left = rightExclusive;
                    break;
                }
            }

            if (factor == null) return null;

            summand.addFactor(operator, factor, locations[operatorIndex].getPosition());
        }

        return summand;
    }

    public FactorNode tryParseFactor(int begin, int endExclusive) {
        var primary = tryParsePrimary(begin, endExclusive);
        if (primary != null) return primary;

        if (begin >= endExclusive - 1) return null;
        if (!tokens[begin].equals(TokenType.Operator, "(")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Operator, ")")) return null;

        return tryParseExpression(begin + 1, endExclusive - 1);
    }

    private MultiplicationOperator tryParseFactorOperator(Token token) {
        if (token.getType() != TokenType.Operator) return null;

        return switch (token.getLexeme()) {
            case "*" -> MultiplicationOperator.MULTIPLY;
            case "/" -> MultiplicationOperator.DIVIDE;
            case "%" -> MultiplicationOperator.MODULO;
            default -> null;
        };
    }

    public PrimaryNode tryParsePrimary(int begin, int endExclusive) {
        var integralLiteral = tryParseIntegralLiteral(begin, endExclusive);
        if (integralLiteral != null) return integralLiteral;

        var realLiteral = tryParseRealLiteral(begin, endExclusive);
        if (realLiteral != null) return realLiteral;

        var routineCall = tryParseRoutineCall(begin, endExclusive);
        if (routineCall != null) return routineCall;

        var booleanLiteral = tryParseBooleanLiteral(begin, endExclusive);
        if (booleanLiteral != null) return booleanLiteral;

        return tryParseModifiablePrimary(begin, endExclusive);
    }

    public IntegralLiteralNode tryParseIntegralLiteral(int begin, int endExclusive) {
        int literalTokenIndex;
        IntegralLiteralNode.Sign sign = null;

        if (begin == endExclusive - 1) {
            literalTokenIndex = begin;
        } else if (begin == endExclusive - 2) {
            literalTokenIndex = begin + 1;

            if (tokens[begin].getType() != TokenType.Operator) return null;

            sign = switch (tokens[begin].getLexeme()) {
                case "+" -> IntegralLiteralNode.Sign.PLUS;
                case "-" -> IntegralLiteralNode.Sign.MINUS;
                case "not" -> IntegralLiteralNode.Sign.NOT;
                default -> null;
            };

            if (sign == null) return null;
        } else {
            return null;
        }

        try {
            var value = Integer.parseInt(tokens[literalTokenIndex].getLexeme());
            return new IntegralLiteralNode(value, sign, locations[literalTokenIndex].getPosition());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public RealLiteralNode tryParseRealLiteral(int begin, int endExclusive) {
        int literalTokenIndex;
        RealLiteralNode.Sign sign = null;

        if (begin == endExclusive - 1) {
            literalTokenIndex = begin;
        } else if (begin == endExclusive - 2) {
            literalTokenIndex = begin + 1;

            if (tokens[begin].getType() != TokenType.Operator) return null;

            sign = switch (tokens[begin].getLexeme()) {
                case "+" -> RealLiteralNode.Sign.PLUS;
                case "-" -> RealLiteralNode.Sign.MINUS;
                default -> null;
            };

            if (sign == null) return null;
        } else {
            return null;
        }

        try {
            var value = Double.parseDouble(tokens[literalTokenIndex].getLexeme());
            return new RealLiteralNode(value, sign, locations[literalTokenIndex].getPosition());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public BooleanLiteralNode tryParseBooleanLiteral(int begin, int endExclusive) {
        if (begin != endExclusive - 1) return null;

        var booleanLiteralType = tryParseBooleanLiteralType(tokens[begin]);
        if (booleanLiteralType != null)
            return BooleanLiteralNode.create(booleanLiteralType, locations[begin].getPosition());

        return null;
    }

    private Boolean tryParseBooleanLiteralType(Token token) {
        if (token.getType() != TokenType.Keyword) return null;

        return switch (token.getLexeme()) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }

    public ModifiablePrimaryNode tryParseModifiablePrimary(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;

        var identifier = tryParseIdentifier(begin, begin + 1);
        if (identifier == null) return null;

        var modifiablePrimary = new ModifiablePrimaryNode(identifier, locations[begin].getPosition());
        var left = begin + 1;

        while (left < endExclusive) {
            var token = tokens[left++];
            if (left >= endExclusive) return null;

            if (token.equals(TokenType.Operator, ".")) {
                var member = tryParseIdentifier(left, left + 1);

                if (member != null) {
                    modifiablePrimary.addMember(member);
                } else if (tokens[left].equals(TokenType.Keyword, "size")) {
                    modifiablePrimary.addArraySize();
                } else {
                    return null;
                }

                left++;
            } else if (token.equals(TokenType.Operator, "[")){
                var closingBracketIndex = getIndexOfFirstStandaloneClosingBracket(left, endExclusive, '[', ']');
                if (closingBracketIndex == -1) return null;

                var indexer = tryParseExpression(left, closingBracketIndex);
                if (indexer == null) return null;

                modifiablePrimary.addIndexer(indexer);
                left = closingBracketIndex + 1;
            } else {
                return null;
            }
        }

        return modifiablePrimary;
    }

    private BinaryRelationNode.Comparison tryParseComparison(Token token) {
        if (token.getType() != TokenType.Operator) return null;

        return switch (token.getLexeme()) {
            case "<" -> BinaryRelationNode.Comparison.LESS;
            case "<=" -> BinaryRelationNode.Comparison.LESS_EQUAL;
            case ">" -> BinaryRelationNode.Comparison.GREATER;
            case ">=" -> BinaryRelationNode.Comparison.GREATER_EQUAL;
            case "=" -> BinaryRelationNode.Comparison.EQUAL;
            case "/=" -> BinaryRelationNode.Comparison.NOT_EQUAL;
            default -> null;
        };
    }

    private AdditionOperator tryParseSimpleNodeOperator(Token token) {
        if (token.getType() != TokenType.Operator) return null;

        return switch (token.getLexeme()) {
            case "+" -> AdditionOperator.PLUS;
            case "-" -> AdditionOperator.MINUS;
            default -> null;
        };
    }

    public TypeNode tryParseType(int begin, int endExclusive) {
        var primitive = tryParsePrimitiveType(begin, endExclusive);
        if (primitive != null) return primitive;

        var array = tryParseArrayType(begin, endExclusive);
        if (array != null) return array;

        var record = tryParseRecordType(begin, endExclusive);
        if (record != null) return record;

        return tryParseIdentifier(begin, endExclusive);
    }

    public PrimitiveTypeNode tryParsePrimitiveType(int begin, int endExclusive) {
        if (begin != endExclusive - 1) return null;

        var token = tokens[begin];
        if (token.getType() != TokenType.Keyword) return null;

        var position = locations[begin].getPosition();

        return switch (token.getLexeme()) {
            case "integer" -> new PrimitiveTypeNode(PrimitiveType.INTEGER, position);
            case "real" -> new PrimitiveTypeNode(PrimitiveType.REAL, position);
            case "boolean" -> new PrimitiveTypeNode(PrimitiveType.BOOLEAN, position);
            default -> null;
        };
    }

    public ArrayTypeNode tryParseArrayType(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "array")) return null;
        if (begin + 1 >= endExclusive) return null;
        if (!tokens[begin + 1].equals(TokenType.Operator, "[")) return null;

        var closingBracketIndex = getIndexOfFirstStandaloneClosingBracket(begin + 2, endExclusive, '[', ']');
        if (closingBracketIndex == -1) return null;

        ExpressionNode size = null;

        if (closingBracketIndex != begin + 2) {
            size = tryParseExpression(begin + 2, closingBracketIndex);
            if (size == null) return null;
        }

        var type = tryParseType(closingBracketIndex + 1, endExclusive);
        if (type == null) return null;

        return new ArrayTypeNode(size, type, locations[begin].getPosition());
    }

    public RecordTypeNode tryParseRecordType(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "record")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) return null;

        var record = new RecordTypeNode(locations[begin].getPosition());
        begin += 1;
        endExclusive -= 1;
        var left = begin;

        while (left < endExclusive) {
            if (tokens[left].getType() == TokenType.DeclarationSeparator) {
                left++;
                continue;
            }

            VariableDeclarationNode variable = null;

            for (int rightExclusive = endExclusive; rightExclusive > left; rightExclusive--) {
                variable = tryParseVariableDeclaration(left, rightExclusive);

                if (variable != null) {
                    record.variables.add(variable);
                    left = rightExclusive;
                    break;
                }
            }

            if (variable == null) return null;
            if (left >= endExclusive) break;
            if (tokens[left].getType() != TokenType.DeclarationSeparator) return null;
        }

        return record;
    }

    public IdentifierNode tryParseIdentifier(int begin, int endExclusive) {
        if (begin != endExclusive - 1) return null;
        var token = tokens[begin];
        if (token.getType() != TokenType.Identifier) return null;

        return new IdentifierNode(token.getLexeme(), locations[begin].getPosition());
    }

    private int getIndexOfFirstStandaloneClosingBracket(int begin, int endExclusive, char opening, char closing) {
        int openedCount = 0;

        for (int index = begin; index < endExclusive; index++) {
            if (tokens[index].equals(TokenType.Operator, Character.toString(closing))) {
                openedCount--;
                if (openedCount == -1) return index;
            } else if (tokens[index].equals(TokenType.Operator, Character.toString(opening))) {
                openedCount++;
            }
        }

        return -1;
    }

    public TypeDeclarationNode tryParseTypeDeclaration(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "type")) return null;
        if (begin + 1 >= endExclusive) return null;

        var identifier = tryParseIdentifier(begin + 1, begin + 2);
        if (identifier == null) return null;

        if (begin + 2 >= endExclusive) return null;
        if (!tokens[begin + 2].equals(TokenType.Keyword, "is")) return null;

        var type = tryParseType(begin + 3, endExclusive);
        if (type == null) return null;

        return new TypeDeclarationNode(identifier, type, locations[begin].getPosition());
    }

    public RoutineDeclarationNode tryParseRoutineDeclaration(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "routine")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) return null;

        var startPosition = locations[begin].getPosition();
        endExclusive--;

        if (begin + 1 >= endExclusive) return null;
        var identifier = tryParseIdentifier(begin + 1, begin + 2);
        if (identifier == null) return null;

        if (begin + 2 >= endExclusive) return null;
        if (!tokens[begin + 2].equals(TokenType.Operator, "(")) return null;

        var matchingParenthesisIndex = getIndexOfFirstStandaloneClosingBracket(begin + 3, endExclusive, '(', ')');
        if (matchingParenthesisIndex == -1) return null;

        var parameters = tryParseParameters(begin + 3, matchingParenthesisIndex);
        if (parameters == null) return null;

        if (matchingParenthesisIndex + 1 >= endExclusive) return null;

        if (tokens[matchingParenthesisIndex + 1].equals(TokenType.Keyword, "is")) {
            var body = tryParseBody(matchingParenthesisIndex + 2, endExclusive);
            if (body == null) return null;

            return new RoutineDeclarationNode(identifier, parameters, body, startPosition);
        } else if (tokens[matchingParenthesisIndex + 1].equals(TokenType.Operator, ":")) {
            for (int isIndex = matchingParenthesisIndex + 2; isIndex < endExclusive; isIndex++) {
                if (!tokens[isIndex].equals(TokenType.Keyword, "is")) continue;

                var returnType = tryParseType(matchingParenthesisIndex + 2, isIndex);
                if (returnType == null) continue;

                var body = tryParseBody(isIndex + 1, endExclusive);
                if (body == null) continue;

                return new RoutineDeclarationNode(identifier, parameters, returnType, body, startPosition);
            }
        }

        return null;
    }

    public ParametersNode tryParseParameters(int begin, int endExclusive) {
        var startPosition = 0 <= begin && begin < tokens.length ? locations[begin].getPosition() : null;
        var parameters = new ParametersNode(startPosition);
        if (begin >= endExclusive) return parameters;

        var left = begin;

        while (left < endExclusive) {
            var commaIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Operator, ",");

            if (commaIndex == -1) {
                if (left + 2 >= endExclusive) return null;
                if (!tokens[left + 1].equals(TokenType.Operator, ":")) return null;

                var identifier = tryParseIdentifier(left, left + 1);
                if (identifier == null) return null;

                var type = tryParseType(left + 2, endExclusive);
                if (type == null) return null;

                parameters.parameters.add(new Pair<>(identifier, type));
                left = endExclusive;
            } else {
                if (left + 2 >= commaIndex) return null;
                if (commaIndex == endExclusive - 1) return null;
                if (!tokens[left + 1].equals(TokenType.Operator, ":")) return null;

                var identifier = tryParseIdentifier(left, left + 1);
                if (identifier == null) return null;

                var type = tryParseType(left + 2, commaIndex);
                if (type == null) return null;

                parameters.parameters.add(new Pair<>(identifier, type));
                left = commaIndex + 1;
            }
        }

        return parameters;
    }

    private int getIndexOfFirstToken(int begin, int endExclusive, TokenType type, String lexeme) {
        for (int index = begin; index < endExclusive; index++) {
            if (tokens[index].equals(type, lexeme))
                return index;
        }

        return -1;
    }

    public BodyNode tryParseBody(int begin, int endExclusive) {
        var body = new BodyNode();
        if (begin >= endExclusive) return body;

        var left = begin;

        while (left < endExclusive) {
            if (tokens[left].getType() == TokenType.DeclarationSeparator) {
                left++;
                continue;
            }

            for (int rightExclusive = left + 1; rightExclusive <= endExclusive; rightExclusive++) {
                if (rightExclusive != endExclusive && tokens[rightExclusive].getType() != TokenType.DeclarationSeparator) continue;
                var statement = tryParseStatement(left, rightExclusive);

                if (statement != null) {
                    body.statements.add(statement);
                    left = rightExclusive;
                    break;
                }
            }

            if (left == endExclusive) return body;
            if (tokens[left].getType() != TokenType.DeclarationSeparator) return null;
        }

        return body;
    }

    public StatementNode tryParseStatement(int begin, int endExclusive) {
        var simpleDeclaration = tryParseSimpleDeclaration(begin, endExclusive);
        if (simpleDeclaration != null) return simpleDeclaration;

        var assignment = tryParseAssignment(begin, endExclusive);
        if (assignment != null) return assignment;

        var routineCall = tryParseRoutineCall(begin, endExclusive);
        if (routineCall != null) return routineCall;

        var whileLoop = tryParseWhileLoop(begin, endExclusive);
        if (whileLoop != null) return whileLoop;

        var forLoop = tryParseForLoop(begin, endExclusive);
        if (forLoop != null) return forLoop;

        var ifStatement = tryParseIfStatement(begin, endExclusive);
        if (ifStatement != null) return ifStatement;

        return tryParseReturn(begin, endExclusive);
    }

    public AssignmentNode tryParseAssignment(int begin, int endExclusive) {
        int assignmentIndex = -1;

        for (int index = begin + 1; index < endExclusive - 1; index++) {
            if (tokens[index].equals(TokenType.Operator, ":=")) {
                assignmentIndex = index;
                break;
            }
        }

        if (assignmentIndex == -1) return null;

        var modifiable = tryParseModifiablePrimary(begin, assignmentIndex);
        if (modifiable == null) return null;

        var expression = tryParseExpression(assignmentIndex + 1, endExclusive);
        if (expression == null) return null;

        return new AssignmentNode(modifiable, expression);
    }

    public RoutineCallNode tryParseRoutineCall(int begin, int endExclusive) {
        if (begin >= endExclusive - 1) return null;

        var name = tryParseIdentifier(begin, begin + 1);
        if (name == null) return null;

        var routineCall = new RoutineCallNode(name, locations[begin].getPosition());

        if (!tokens[begin + 1].equals(TokenType.Operator, "(")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Operator, ")")) return null;

        var left = begin + 2;
        endExclusive--;

        while (left < endExclusive) {
            var commaIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Operator, ",");

            if (commaIndex == -1) {
                var argument = tryParseExpression(left, endExclusive);
                if (argument == null) return null;

                routineCall.arguments.add(argument);
                left = endExclusive;
            } else {
                var argument = tryParseExpression(left, commaIndex);
                if (argument == null) return null;

                routineCall.arguments.add(argument);
                left = commaIndex + 1;
                if (left == endExclusive) return null;
            }
        }

        return routineCall;
    }

    public WhileLoopNode tryParseWhileLoop(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "while")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) return null;

        var loopTokenIndex = -1;

        for (int index = begin + 1; index < endExclusive - 1; index++) {
            if (tokens[index].equals(TokenType.Keyword, "loop"))
            {
                loopTokenIndex = index;
                break;
            }
        }

        if (loopTokenIndex == -1) return null;

        var condition = tryParseExpression(begin + 1, loopTokenIndex);
        if (condition == null) return null;

        var body = tryParseBody(loopTokenIndex + 1, endExclusive - 1);
        if (body == null) return null;

        return new WhileLoopNode(condition, body, locations[begin].getPosition());
    }

    public ForLoopNode tryParseForLoop(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "for")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) return null;

        int left = begin + 1;
        endExclusive--;

        var variable = tryParseIdentifier(left, left + 1);
        if (variable == null) return null;

        left++;
        var loopIndex = -1;

        for (int index = left; index < endExclusive; index++) {
            if (tokens[index].equals(TokenType.Keyword, "loop")) {
                loopIndex = index;
                break;
            }
        }

        if (loopIndex == -1) return null;

        var range = tryParseRange(left, loopIndex);
        if (range == null) return null;

        var body = tryParseBody(loopIndex + 1, endExclusive);
        if (body == null) return null;

        return new ForLoopNode(variable, range, body, locations[begin].getPosition());
    }

    public RangeNode tryParseRange(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "in")) return null;

        int left = begin + 1;
        if (left >= endExclusive) return null;

        boolean reverse = false;

        if (tokens[left].equals(TokenType.Keyword, "reverse")) {
            reverse = true;
            left++;
        }

        var dotsIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Operator, "..");
        if (dotsIndex == -1) return null;

        var from = tryParseExpression(left, dotsIndex);
        if (from == null) return null;

        var to = tryParseExpression(dotsIndex + 1, endExclusive);
        if (to == null) return null;

        return new RangeNode(from, to, reverse, locations[begin].getPosition());
    }

    public IfStatementNode tryParseIfStatement(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "if")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) return null;

        int left = begin + 1;
        endExclusive--;

        var thenIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Keyword, "then");
        if (thenIndex == -1) return null;

        var condition = tryParseExpression(left, thenIndex);
        if (condition == null) return null;

        for (int elseIndex = thenIndex + 1; elseIndex < endExclusive; elseIndex++) {
            if (tokens[elseIndex].equals(TokenType.Keyword, "else")) {
                var body = tryParseBody(thenIndex + 1, elseIndex);
                if (body == null) continue;

                var elseBody = tryParseBody(elseIndex + 1, endExclusive);
                if (elseBody == null) continue;

                return new IfStatementNode(condition, body, elseBody, locations[begin].getPosition());
            }
        }

        var body = tryParseBody(thenIndex + 1, endExclusive);
        if (body == null) return null;

        return new IfStatementNode(condition, body, null, locations[begin].getPosition());
    }

    public ReturnStatementNode tryParseReturn(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "return")) return null;

        if (begin == endExclusive - 1) return new ReturnStatementNode(locations[begin].getPosition());

        var expression = tryParseExpression(begin + 1, endExclusive);
        if (expression == null) return null;

        return new ReturnStatementNode(expression, locations[begin].getPosition());
    }

    public Parser(Token[] tokens, StringWithLocation[] locations) {
        this.tokens = tokens;
        this.locations = locations;
    }

    public int getTokensCount() {
        return tokens.length;
    }

    private final Token[] tokens;
    private final StringWithLocation[] locations;
}
