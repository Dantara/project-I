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
import projectI.CodePosition;
import projectI.Lexer.StringWithLocation;
import projectI.Lexer.Token;
import projectI.Lexer.TokenType;
import projectI.Parser.Errors.*;

import java.util.*;

/**
 * A stage of compilation that takes the token produced by lexical analysis as input and generates a parse tree (or syntax tree)
 */
public class Parser {

    /**
     * Parse the program
     * @return a program node if it can be parsed otherwise null object
     */
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

            if (declaration == null) {
                if (getErrorCount() == 0)
                    expectedDeclaration(left, endExclusive);

                return null;
            }

            errors.clear();
            program.declarations.add(declaration);
            declaration.setParent(program);

            if (left == endExclusive) break;
        }

        if (getErrorCount() > 0)
            return null;

        return program;
    }

    private void expectedDeclaration(int begin, int endExclusive) {
        var endPosition = locations[endExclusive - 1].getPosition();
        var lastTokenLength = tokens[endExclusive - 1].getLexeme().length();
        endPosition = new CodePosition(endPosition.lineIndex, endPosition.beginningIndex + lastTokenLength);
        errors.add(new ExpectedDeclarationError(locations[begin].getPosition(), endPosition));
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

    /**
     * Parse a variable declaration
     * @param begin is an index of token with which the variable declaration begins
     * @param endExclusive is an index of token with which the variable declaration ends
     * @return a Variable Declaration Node if it can be parsed otherwise null object
     */
    public VariableDeclarationNode tryParseVariableDeclaration(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "var")) return null;

        var position = locations[begin].getPosition();

        var identifierIndex = begin + 1;
        if (identifierIndex >= endExclusive) return null;

        if (tokens[identifierIndex].getType() != TokenType.Identifier) {
            expectedIdentifierAt(identifierIndex);
            return null;
        }

        var identifier = tryParseIdentifier(identifierIndex, identifierIndex + 1);
        if (identifier == null) return null;

        if (begin + 2 >= endExclusive) return null;

        if (tokens[begin + 2].equals(TokenType.Operator, ":")) {
            var isIndex = getIndexOfFirstToken(begin + 3, endExclusive, TokenType.Keyword, "is");

            if (isIndex != -1) {
                for (isIndex = endExclusive - 1; isIndex >= begin; isIndex--) {
                    if (!tokens[isIndex].equals(TokenType.Keyword, "is")) continue;

                    var type = tryParseType(begin + 3, isIndex);
                    if (type == null) continue;
                    var expression = tryParseExpression(isIndex + 1, endExclusive);
                    if (expression == null) continue;

                    var variable = new VariableDeclarationNode(identifier, type, expression, position);
                    identifier.setParent(variable);
                    type.setParent(variable);
                    expression.setParent(variable);

                    return variable;
                }
            }

            var type = tryParseType(begin + 3, endExclusive);
            if (type == null) return null;

            var variable = new VariableDeclarationNode(identifier, type, null, position);
            identifier.setParent(variable);
            type.setParent(variable);

            return variable;
        } else if (tokens[begin + 2].equals(TokenType.Keyword, "is")) {
            var expression = tryParseExpression(begin + 3, endExclusive);
            if (expression == null) return null;

            var variable = new VariableDeclarationNode(identifier, null, expression, position);
            identifier.setParent(variable);
            expression.setParent(variable);

            return variable;
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

    /**
     * Parse an expression
     * @param begin is an index of token with which the expression begins
     * @param endExclusive is an index of token with which the expression ends
     * @return an Expression Node if it can be parsed otherwise null object
     */
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
        relation.setParent(expression);

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
            relation.setParent(expression);
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

    /**
     * Parse a relation
     * @param begin is an index of toke with which the relation begins
     * @param endExclusive is an index of toke with which the relation ends
     * @return a Relation Node if it can be parsed otherwise null object
     */
    public RelationNode tryParseRelation(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (tokens[begin].equals(TokenType.Operator, "not")) {
            var innerRelation = tryParseRelation(begin + 1, endExclusive);
            if (innerRelation == null) return null;

            var negatedRelation = new NegatedRelationNode(innerRelation, locations[begin].getPosition());
            innerRelation.setParent(negatedRelation);

            return negatedRelation;
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
        if (left >= endExclusive) {
            var binaryRelation = new BinaryRelationNode(simple);
            simple.setParent(binaryRelation);
            return binaryRelation;
        }

        var comparison = tryParseComparison(tokens[left]);
        if (comparison == null) return null;

        if (left + 1 >= endExclusive) return null;

        var otherSimple = tryParseSimple(left + 1, endExclusive);
        if (otherSimple == null) return null;

        var binaryRelation = new BinaryRelationNode(simple, comparison, otherSimple, locations[left].getPosition());
        simple.setParent(binaryRelation);
        otherSimple.setParent(binaryRelation);
        return binaryRelation;
    }

    /**
     * Parse a Simple
     * @param begin is an index of toke with which the simple begins
     * @param endExclusive is an index of toke with which the simple ends
     * @return a Simple Node if it can be parsed otherwise null object
     */
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
        factor.setParent(simple);

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
            factor.setParent(simple);
        }

        return simple;
    }

    /**
     * Parse a Summand
     * @param begin is an index of toke with which the summand begins
     * @param endExclusive is an index of toke with which the summand ends
     * @return a Summand Node if it can be parsed otherwise null object
     */
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
        factor.setParent(summand);

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
            factor.setParent(summand);
        }

        return summand;
    }

    /**
     * Parse a factor
     * @param begin is an index of toke with which the factor begins
     * @param endExclusive is an index of toke with which the factor ends
     * @return a Factor Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse a Primary
     * @param begin is an index of toke with which the primary begins
     * @param endExclusive is an index of toke with which the primary ends
     * @return a Primary Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse a Integral Literal
     * @param begin is an index of toke with which the integral literal begins
     * @param endExclusive is an index of toke with which the integral literal ends
     * @return a Integral Literal Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse a Real Literal
     * @param begin is an index of toke with which the real literal begins
     * @param endExclusive is an index of toke with which the real literal ends
     * @return a Real Literal Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse a Boolean Literal
     * @param begin is an index of toke with which the boolean literal begins
     * @param endExclusive is an index of toke with which the boolean literal ends
     * @return a Boolean Literal Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse a Modifiable Primary
     * @param begin is an index of toke with which the modifiable primary begins
     * @param endExclusive is an index of toke with which the modifiable primary ends
     * @return a Modifiable Primary Node if it can be parsed otherwise null object
     */
    public ModifiablePrimaryNode tryParseModifiablePrimary(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;

        var identifier = tryParseIdentifier(begin, begin + 1);
        if (identifier == null) return null;

        var modifiablePrimary = new ModifiablePrimaryNode(identifier, locations[begin].getPosition());
        identifier.setParent(modifiablePrimary);
        var left = begin + 1;

        while (left < endExclusive) {
            var token = tokens[left++];
            if (left >= endExclusive) return null;

            if (token.equals(TokenType.Operator, ".")) {
                var member = tryParseIdentifier(left, left + 1);

                if (member != null) {
                    modifiablePrimary.addMember(member);
                    member.setParent(modifiablePrimary);
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
                indexer.setParent(modifiablePrimary);
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

    /**
     * Parse a Type
     * @param begin is an index of toke with which the type begins
     * @param endExclusive is an index of toke with which the type ends
     * @return a Type Node if it can be parsed otherwise null object
     */
    public TypeNode tryParseType(int begin, int endExclusive) {
        var primitive = tryParsePrimitiveType(begin, endExclusive);
        if (primitive != null) return primitive;

        var array = tryParseArrayType(begin, endExclusive);
        if (array != null) return array;

        var record = tryParseRecordType(begin, endExclusive);
        if (record != null) return record;

        return tryParseIdentifier(begin, endExclusive);
    }

    /**
     * Parse a Primitive Type
     * @param begin is an index of toke with which the primitive type begins
     * @param endExclusive is an index of toke with which the primitive type ends
     * @return a Primitive Type Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse an Array Type
     * @param begin is an index of toke with which the array type begins
     * @param endExclusive is an index of toke with which the array type ends
     * @return an Array Type Node if it can be parsed otherwise null object
     */
    public ArrayTypeNode tryParseArrayType(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "array")) return null;
        if (begin + 1 >= endExclusive) return null;
        if (!tokens[begin + 1].equals(TokenType.Operator, "[")) {
            expectedOperator("[", begin);
            return null;
        }

        var closingBracketIndex = getIndexOfFirstStandaloneClosingBracket(begin + 2, endExclusive, '[', ']');
        if (closingBracketIndex == -1) {
            expectedOperator("]", begin + 1);
            return null;
        }

        ExpressionNode size = null;

        if (closingBracketIndex != begin + 2) {
            size = tryParseExpression(begin + 2, closingBracketIndex);
            if (size == null) {
                expectedExpression(begin + 2, closingBracketIndex);
                return null;
            }
        }

        var type = tryParseType(closingBracketIndex + 1, endExclusive);
        if (type == null) {
            expectedType(closingBracketIndex + 1, endExclusive);
            return null;
        }

        var array = new ArrayTypeNode(size, type, locations[begin].getPosition());

        type.setParent(array);
        if (size != null)
            size.setParent(array);


        return array;
    }
    
    private void expectedExpression(int begin, int endExclusive) {
        var tokens = Arrays.copyOfRange(this.tokens, begin, endExclusive);
        errors.add(new ExpectedExpressionError(tokens, locations[begin].getPosition()));
    }

    /**
     * Parse a Record Type
     * @param begin is an index of toke with which the record type begins
     * @param endExclusive is an index of toke with which the record type ends
     * @return a Record Type Node if it can be parsed otherwise null object
     */
    public RecordTypeNode tryParseRecordType(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "record")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) {
            expectedKeyword("end", endExclusive - 1);
            return null;
        }

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
                    variable.setParent(record);
                    left = rightExclusive;
                    break;
                }
            }

            if (variable == null) {
                expectedVariableDeclaration(left, endExclusive);
                return null;
            }
            if (left >= endExclusive) break;
            if (tokens[left].getType() != TokenType.DeclarationSeparator) return null;
        }

        return record;
    }

    private void expectedVariableDeclaration(int begin, int endExclusive) {
        var tokens = Arrays.copyOfRange(this.tokens, begin, endExclusive);
        var position = locations[begin].getPosition();
        var endTokenPosition = locations[endExclusive - 1].getPosition();
        var endTokenLength = this.tokens[endExclusive - 1].getLexeme().length();
        var end = new CodePosition(endTokenPosition.lineIndex, endTokenPosition.beginningIndex + endTokenLength);
        errors.add(new ExpectedVariableDeclarationError(tokens, position, end));
    }

    /**
     * Parse an Identifier
     * @param begin is an index of toke with which the identifier begins
     * @param endExclusive is an index of toke with which the identifier ends
     * @return an Idintifier Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse a Declaration
     * @param begin is an index of toke with which the declarartion begins
     * @param endExclusive is an index of toke with which the declaration ends
     * @return a Declaration Node if it can be parsed otherwise null object
     */
    public TypeDeclarationNode tryParseTypeDeclaration(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "type")) return null;

        var identifierIndex = begin + 1;
        if (identifierIndex >= endExclusive) return null;

        var identifier = tryParseIdentifier(identifierIndex, identifierIndex + 1);
        if (identifier == null) {
            expectedIdentifierAt(identifierIndex);
            return null;
        }

        if (begin + 2 >= endExclusive) return null;
        if (!tokens[begin + 2].equals(TokenType.Keyword, "is")) return null;

        var type = tryParseType(begin + 3, endExclusive);
        if (type == null) return null;

        var declaration = new TypeDeclarationNode(identifier, type, locations[begin].getPosition());
        identifier.setParent(declaration);
        type.setParent(declaration);

        return declaration;
    }


    private void expectedIdentifierAt(int tokenIndex) {
        errors.add(new ExpectedIdentifierError(tokens[tokenIndex], locations[tokenIndex].getPosition()));
    }

     /**
     * Parse a Routine Declaration
     * @param begin is an index of toke with which the routine declarartion begins
     * @param endExclusive is an index of toke with which the routine declaration ends
     * @return a Routine Declaration Node if it can be parsed otherwise null object
     */
    public RoutineDeclarationNode tryParseRoutineDeclaration(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "routine")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) {
            expectedKeyword("end", endExclusive - 1);
            return null;
        }

        var startPosition = locations[begin].getPosition();
        endExclusive--;

        if (begin + 1 >= endExclusive) return null;
        var identifier = tryParseIdentifier(begin + 1, begin + 2);
        if (identifier == null) {
            expectedIdentifierAt(begin + 1);
            return null;
        }

        if (begin + 2 >= endExclusive) return null;
        if (!tokens[begin + 2].equals(TokenType.Operator, "(")) {
            expectedOperator("(", begin + 1);
            return null;
        }

        var matchingParenthesisIndex = getIndexOfFirstStandaloneClosingBracket(begin + 3, endExclusive, '(', ')');
        if (matchingParenthesisIndex == -1) {
            expectedOperator(")", begin + 2);
            return null;
        }

        var parameters = tryParseParameters(begin + 3, matchingParenthesisIndex);
        if (parameters == null) return null;

        if (matchingParenthesisIndex + 1 >= endExclusive) {
            expectedKeyword("is", matchingParenthesisIndex);
            return null;
        }

        if (tokens[matchingParenthesisIndex + 1].equals(TokenType.Keyword, "is")) {
            var body = tryParseBody(matchingParenthesisIndex + 2, endExclusive);
            if (body == null) return null;

            var routine = new RoutineDeclarationNode(identifier, parameters, body, startPosition);
            body.setParent(routine);
            identifier.setParent(routine);
            parameters.setParent(routine);

            return routine;
        } else if (tokens[matchingParenthesisIndex + 1].equals(TokenType.Operator, ":")) {
            for (int isIndex = matchingParenthesisIndex + 2; isIndex < endExclusive; isIndex++) {
                if (!tokens[isIndex].equals(TokenType.Keyword, "is")) continue;

                var returnType = tryParseType(matchingParenthesisIndex + 2, isIndex);
                if (returnType == null) {
                    expectedType(matchingParenthesisIndex + 2, isIndex);
                    return null;
                }

                var body = tryParseBody(isIndex + 1, endExclusive);
                if (body == null) return null;

                var routine = new RoutineDeclarationNode(identifier, parameters, returnType, body, startPosition);
                body.setParent(routine);
                identifier.setParent(routine);
                parameters.setParent(routine);
                returnType.setParent(routine);

                return routine;
            }
        }

        expectedKeyword("is", matchingParenthesisIndex);
        return null;
    }

    private void expectedKeyword(String keyword, int previousTokenIndex) {
        var previousPosition = locations[previousTokenIndex].getPosition();
        var previousTokenLength = tokens[previousTokenIndex].getLexeme().length();
        var position = new CodePosition(previousPosition.lineIndex, previousPosition.beginningIndex + previousTokenLength + 1);
        errors.add(new ExpectedKeywordError(keyword, position));
    }

    /**
     * Parse Parameters of a routine
     * @param begin is an index of toke with which parameters begins
     * @param endExclusive is an index of toke with which parameters ends
     * @return a Parameters Node if it can be parsed otherwise null object
     */
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
                if (identifier == null) {
                    expectedIdentifierAt(left);
                    return null;
                }

                var type = tryParseType(left + 2, endExclusive);
                if (type == null) {
                    expectedType(left + 2, endExclusive);
                    return null;
                }

                parameters.parameters.add(new Pair<>(identifier, type));

                identifier.setParent(parameters);
                type.setParent(parameters);

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

                identifier.setParent(parameters);
                type.setParent(parameters);

                left = commaIndex + 1;
            }
        }

        return parameters;
    }

    private void expectedType(int begin, int endExclusive) {
        var tokens = Arrays.copyOfRange(this.tokens, begin, endExclusive);
        errors.add(new ExpectedTypeError(tokens, locations[begin].getPosition()));
    }

    private int getIndexOfFirstToken(int begin, int endExclusive, TokenType type, String lexeme) {
        for (int index = begin; index < endExclusive; index++) {
            if (tokens[index].equals(type, lexeme))
                return index;
        }

        return -1;
    }

    /**
     * Parse a Body of a routine
     * @param begin is an index of toke with which the body begins
     * @param endExclusive is an index of toke with which the body ends
     * @return a Body Node if it can be parsed otherwise null object
     */
    public BodyNode tryParseBody(int begin, int endExclusive) {
        var body = new BodyNode();
        if (begin >= endExclusive) return body;

        var left = begin;

        while (left < endExclusive) {
            if (tokens[left].getType() == TokenType.DeclarationSeparator) {
                left++;
                continue;
            }

            var foundStatement = false;

            for (int rightExclusive = left + 1; rightExclusive <= endExclusive; rightExclusive++) {
                if (rightExclusive != endExclusive && tokens[rightExclusive].getType() != TokenType.DeclarationSeparator) continue;
                var statement = tryParseStatement(left, rightExclusive);

                if (statement != null) {
                    body.statements.add(statement);
                    statement.setParent(body);
                    errors.clear();
                    left = rightExclusive;
                    foundStatement = true;
                    break;
                }
            }

            if (!foundStatement) {
                expectedStatement(left, endExclusive);
                return null;
            }
            if (left == endExclusive) return body;
        }

        return body;
    }


    private void expectedStatement(int begin, int endExclusive) {
        var position = locations[begin].getPosition();
        var end = locations[endExclusive - 1].getPosition();
        var lastTokenLength = tokens[endExclusive - 1].getLexeme().length();
        end = new CodePosition(end.lineIndex, end.beginningIndex + lastTokenLength);

        errors.add(new ExpectedStatementError(position, end));
    }

    /**
     * Parse a Statement
     * @param begin is an index of toke with which the statement begins
     * @param endExclusive is an index of toke with which the statement ends
     * @return a Statement Node if it can be parsed otherwise null object
     */
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

    /**
     * Parse an Assignment
     * @param begin is an index of toke with which the assignment begins
     * @param endExclusive is an index of toke with which the assignment ends
     * @return a Assignment Node if it can be parsed otherwise null object
     */
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
        if (modifiable == null) {
            expectedModifiablePrimary(begin, assignmentIndex);
            return null;
        }

        var expression = tryParseExpression(assignmentIndex + 1, endExclusive);
        if (expression == null) {
            expectedExpression(assignmentIndex + 1, endExclusive);
            return null;
        }

        var assignmentNode = new AssignmentNode(modifiable, expression);

        modifiable.setParent(assignmentNode);
        expression.setParent(assignmentNode);

        return assignmentNode;
    }


    private void expectedModifiablePrimary(int begin, int endExclusive) {
        var tokens = Arrays.copyOfRange(this.tokens, begin, endExclusive);
        errors.add(new ExpectedModifiablePrimaryError(tokens, locations[begin].getPosition()));
    }

    /**
     * Parse a Routine Call
     * @param begin is an index of toke with which the routine call begins
     * @param endExclusive is an index of toke with which the routine call ends
     * @return a Routine Call Node if it can be parsed otherwise null object
     */
    public RoutineCallNode tryParseRoutineCall(int begin, int endExclusive) {
        if (begin >= endExclusive - 1) return null;

        var name = tryParseIdentifier(begin, begin + 1);
        if (name == null) return null;

        var routineCall = new RoutineCallNode(name, locations[begin].getPosition());
        name.setParent(routineCall);

        if (!tokens[begin + 1].equals(TokenType.Operator, "(")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Operator, ")")) {
            expectedOperator(")", endExclusive - 1);
            return null;
        }

        var left = begin + 2;
        endExclusive--;

        while (left < endExclusive) {
            var commaIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Operator, ",");

            if (commaIndex == -1) {
                var argument = tryParseExpression(left, endExclusive);
                if (argument == null) {
                    expectedExpression(left, endExclusive);
                    return null;
                }

                routineCall.arguments.add(argument);
                argument.setParent(routineCall);
                left = endExclusive;
            } else {
                var argument = tryParseExpression(left, commaIndex);
                if (argument == null) {
                    expectedExpression(left, commaIndex);
                    return null;
                }

                routineCall.arguments.add(argument);
                argument.setParent(routineCall);
                left = commaIndex + 1;
                if (left == endExclusive) return null;
            }
        }

        return routineCall;
    }

    private void expectedOperator(String operator, int previousTokenIndex) {
        var position = locations[previousTokenIndex].getPosition();
        var tokenLength = tokens[previousTokenIndex].getLexeme().length();
        position = new CodePosition(position.lineIndex, position.beginningIndex + tokenLength);
        errors.add(new ExpectedOperatorError(operator, position));
    }

    /**
     * Parse a While Loop
     * @param begin is an index of toke with which the while loop begins
     * @param endExclusive is an index of toke with which the while loop ends
     * @return a While Loop Node if it can be parsed otherwise null object
     */
    public WhileLoopNode tryParseWhileLoop(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "while")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) {
            expectedKeyword("end", endExclusive - 1);
            return null;
        }

        var loopTokenIndex = -1;

        for (int index = begin + 1; index < endExclusive - 1; index++) {
            if (tokens[index].equals(TokenType.Keyword, "loop"))
            {
                loopTokenIndex = index;
                break;
            }
        }

        if (loopTokenIndex == -1) {
            expectedKeyword("loop", begin + 1);
            return null;
        }

        var condition = tryParseExpression(begin + 1, loopTokenIndex);
        if (condition == null) {
            expectedExpression(begin + 1, loopTokenIndex);
            return null;
        }

        var body = tryParseBody(loopTokenIndex + 1, endExclusive - 1);
        if (body == null) return null;

        var whileLoop = new WhileLoopNode(condition, body, locations[begin].getPosition());
        condition.setParent(whileLoop);
        body.setParent(whileLoop);

        return whileLoop;
    }

    /**
     * Parse a For Loop
     * @param begin is an index of toke with which the for loop begins
     * @param endExclusive is an index of toke with which the for loop ends
     * @return a For Loop Node if it can be parsed otherwise null object
     */
    public ForLoopNode tryParseForLoop(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "for")) return null;
        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) {
            expectedKeyword("end", endExclusive - 1);
            return null;
        }

        int left = begin + 1;
        endExclusive--;

        var variable = tryParseIdentifier(left, left + 1);
        if (variable == null) {
            expectedIdentifierAt(left);
            return null;
        }

        left++;
        var loopIndex = -1;

        for (int index = left; index < endExclusive; index++) {
            if (tokens[index].equals(TokenType.Keyword, "loop")) {
                loopIndex = index;
                break;
            }
        }

        if (loopIndex == -1) {
            expectedKeyword("loop", endExclusive - 1);
            return null;
        }

        var range = tryParseRange(left, loopIndex);
        if (range == null) {
            expectedRange(left, loopIndex);
            return null;
        }

        var body = tryParseBody(loopIndex + 1, endExclusive);
        if (body == null) return null;

        var forLoop = new ForLoopNode(variable, range, body, locations[begin].getPosition());
        variable.setParent(forLoop);
        range.setParent(forLoop);
        body.setParent(forLoop);

        return forLoop;
    }

    private void expectedRange(int begin, int endExclusive) {
        var tokens = Arrays.copyOfRange(this.tokens, begin, endExclusive);
        var position = locations[begin].getPosition();
        CodePosition end;

        if (begin == endExclusive) {
            end = position;
        } else {
            end = locations[endExclusive - 1].getPosition();
            var tokenLength = this.tokens[endExclusive - 1].getLexeme().length();
            end = new CodePosition(end.lineIndex, end.beginningIndex + tokenLength);
        }

        errors.add(new ExpectedRangeError(tokens, position, end));
    }

    /**
     * Parse a Range of For Loop
     * @param begin is an index of toke with which the range begins
     * @param endExclusive is an index of toke with which the range ends
     * @return a Range Node if it can be parsed otherwise null object
     */
    public RangeNode tryParseRange(int begin, int endExclusive) {
        if (begin >= endExclusive) {
            expectedKeyword("in", begin - 1);
            return null;
        }
        if (!tokens[begin].equals(TokenType.Keyword, "in")) {
            expectedKeyword("in", begin - 1);
            return null;
        }

        int left = begin + 1;
        if (left >= endExclusive) return null;

        boolean reverse = false;

        if (tokens[left].equals(TokenType.Keyword, "reverse")) {
            reverse = true;
            left++;
        }

        var dotsIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Operator, "..");
        if (dotsIndex == -1) {
            expectedOperator("..", left);
            return null;
        }

        var from = tryParseExpression(left, dotsIndex);
        if (from == null) {
            expectedExpression(left, dotsIndex);
            return null;
        }

        var to = tryParseExpression(dotsIndex + 1, endExclusive);
        if (to == null) {
            expectedExpression(dotsIndex + 1, endExclusive);
            return null;
        }

        var range = new RangeNode(from, to, reverse, locations[begin].getPosition());
        from.setParent(range);
        to.setParent(range);

        return range;
    }

    /**
     * Parse an If Statement
     * @param begin is an index of toke with which the if statement begins
     * @param endExclusive is an index of toke with which the if statement ends
     * @return an If Statement Node if it can be parsed otherwise null object
     */
    public IfStatementNode tryParseIfStatement(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "if")) return null;

        if (!tokens[endExclusive - 1].equals(TokenType.Keyword, "end")) {
            expectedKeyword("end", endExclusive - 1);
            return null;
        }

        int left = begin + 1;
        endExclusive--;

        var thenIndex = getIndexOfFirstToken(left, endExclusive, TokenType.Keyword, "then");
        if (thenIndex == -1) {
            expectedKeyword("then", endExclusive - 1);
            return null;
        }

        var condition = tryParseExpression(left, thenIndex);
        if (condition == null) return null;

        for (int elseIndex = thenIndex + 1; elseIndex < endExclusive; elseIndex++) {
            if (tokens[elseIndex].equals(TokenType.Keyword, "else")) {
                var body = tryParseBody(thenIndex + 1, elseIndex);
                if (body == null) continue;

                var elseBody = tryParseBody(elseIndex + 1, endExclusive);
                if (elseBody == null) continue;

                var statement = new IfStatementNode(condition, body, elseBody, locations[begin].getPosition());

                condition.setParent(statement);
                body.setParent(statement);
                elseBody.setParent(statement);

                return statement;
            }
        }

        var body = tryParseBody(thenIndex + 1, endExclusive);
        if (body == null) return null;

        var statement = new IfStatementNode(condition, body, null, locations[begin].getPosition());

        condition.setParent(statement);
        body.setParent(statement);

        return statement;
    }

    /**
     * Parse a Return Statement
     * @param begin is an index of toke with which the return statement begins
     * @param endExclusive is an index of toke with which the return statement ends
     * @return a Return Statement Node if it can be parsed otherwise null object
     */
    public ReturnStatementNode tryParseReturn(int begin, int endExclusive) {
        if (begin >= endExclusive) return null;
        if (!tokens[begin].equals(TokenType.Keyword, "return")) return null;

        if (begin == endExclusive - 1) return new ReturnStatementNode(locations[begin].getPosition());

        var expression = tryParseExpression(begin + 1, endExclusive);
        if (expression == null) return null;

        var returnStatement = new ReturnStatementNode(expression, locations[begin].getPosition());
        expression.setParent(returnStatement);

        return returnStatement;
    }

    /**
     * A constructor for initializing objects of class Parser
     * @param tokens is a list of tokes that will be used for parsing
     * @param locations is a list of lexemes with their locations in source code
     */
    public Parser(Token[] tokens, StringWithLocation[] locations) {
        this.tokens = tokens;
        this.locations = locations;
    }

    /**
     * Returns the number of tokens
     * @return size of tokes list
     */
    public int getTokensCount() {
        return tokens.length;
    }

    public int getErrorCount() {
        return errors.size();
    }

    public List<ParsingError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private final Token[] tokens;
    private final StringWithLocation[] locations;
    private final List<ParsingError> errors = new ArrayList<>();
}
