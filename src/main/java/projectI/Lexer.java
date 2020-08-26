package projectI;

import org.javatuples.Pair;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A stage of compilation that splits the given source code into a sequence of tokens.
 */
public class Lexer {
    private List<StringWithLocation> words;

    /**
     * Create an array with lexeme + its location for each token (the last scan is considered).
     * @return lexemes with locations.
     */
    public StringWithLocation[] getLexemesWithLocations() {
        var stringWithLocations = new StringWithLocation[words.size()];
        words.toArray(stringWithLocations);
        return stringWithLocations;
    }

    /**
     * Split the program text in a sequence of tokens.
     * @param programText source code to scan
     * @return Sequence of tokens
     * @throws InvalidLexemeException when lexer is unable to recognize a token
     */
    public Token[] scan(String programText) throws InvalidLexemeException {
        programText = changeNewLinesToConventional(programText);
        words = splitToWords(programText);
        separateDeclarations(words);

        for (int order = 0; order < operatorOrders.size(); order++) {
            separateSymbolicOperators(words, order);
        }

        return toTokens();
    }

    private static String changeNewLinesToConventional(String programText) {
        return programText.replaceAll("\r\n", "\n");
    }

    /**
     * Generate a list of words storing their locations
     * @param programText source code
     * @return a list of lexemes with locations
     */
    private List<StringWithLocation> splitToWords(String programText) {
        var strings = new ArrayList<StringWithLocation>();
        var buffer = new StringBuilder();

        var lineIndex = 0;
        var wordBeginningIndex = 0;

        for (var character : programText.toCharArray()) {
            if (character == ' ' || character == '\n' || character == '\t') {
                int offset = 1;

                if (buffer.length() > 0) {
                    var word = buffer.toString();
                    offset += word.length();
                    strings.add(new StringWithLocation(word, lineIndex, wordBeginningIndex));
                    buffer.setLength(0);
                }

                wordBeginningIndex += offset;

                if (character == '\n') {
                    strings.add(new StringWithLocation("\n", lineIndex, wordBeginningIndex - 1));
                    wordBeginningIndex = 0;
                    lineIndex++;
                }
            } else {
                buffer.append(character);
            }
        }

        if (buffer.length() > 0) {
            var word = buffer.toString();
            strings.add(new StringWithLocation(word, lineIndex, wordBeginningIndex));
        }

        return strings;
    }

    /**
     * Split words containing declaration character(s).
     * @param words a list of lexemes with locations
     */
    private void separateDeclarations(List<StringWithLocation> words) {
        for (int index = 0; index < words.size(); index++) {
            var word = words.get(index);
            var separatorIndex = getIndexOfDeclarationSeparator(word.getString());
            if (separatorIndex == -1 || word.getString().length() == 1) continue;

            var left = word.getString().substring(0, separatorIndex);
            var separator = word.getString().charAt(separatorIndex);
            var right = word.getString().substring(separatorIndex + 1);

            words.set(index, new StringWithLocation(left, word.getLineIndex(), word.getBeginningIndex()));
            words.add(index + 1, new StringWithLocation(Character.toString(separator), word.getLineIndex(), word.getBeginningIndex() + left.length()));
            words.add(index + 2, new StringWithLocation(right, word.getLineIndex(), word.getBeginningIndex() + left.length() + 1));
        }

        removeBlank(words);
    }

    private static int getIndexOfDeclarationSeparator(String str) {
        var index = -1;

        for (char separator: declarationSeparators) {
            var separatorIndex = str.indexOf(separator);
            if (separatorIndex == -1) continue;
            if (index != -1 && separatorIndex > index) continue;

            index = separatorIndex;
        }

        return index;
    }

    private static void removeBlank(List<StringWithLocation> strings) {
        for (int index = strings.size() - 1; index >= 0; index--) {
            if (strings.get(index).getString().isEmpty())
                strings.remove(index);
        }
    }

    /**
     * Split words containing operators (only those that cannot be a part of an identifier).
     * @param words a list of lexemes with locations
     * @param order the order of operators to consider
     */
    private static void separateSymbolicOperators(List<StringWithLocation> words, int order) {
        for (int index = 0; index < words.size(); index++) {
            var word = words.get(index);
            if (isIntegerLiteral(word.getString()) || isRealLiteral(word.getString())) continue;

            var position = getPositionOfSymbolicOperator(word.getString(), order);
            var operatorIndex = position.getValue0();
            var operatorLength = position.getValue1();
            if (operatorIndex == -1 || operatorLength == word.getString().length()) continue;

            var left = word.getString().substring(0, operatorIndex);
            var operator = word.getString().substring(operatorIndex, operatorIndex + operatorLength);
            var right = word.getString().substring(operatorIndex + operatorLength);

            words.set(index, new StringWithLocation(left, word.getLineIndex(), word.getBeginningIndex()));
            words.add(index + 1, new StringWithLocation(operator, word.getLineIndex(), word.getBeginningIndex() + left.length()));
            words.add(index + 2, new StringWithLocation(right, word.getLineIndex(), word.getBeginningIndex() + left.length() + operator.length()));
        }

        removeBlank(words);
    }

    private static Pair<Integer, Integer> getPositionOfSymbolicOperator(String word, int order) {
        int index = -1;
        int length = 0;

        for (int operatorsIndex = 0; operatorsIndex < operatorOrders.size() && operatorsIndex <= order; operatorsIndex++) {
            for (String operator : operatorOrders.get(operatorsIndex)) {
                if (!isSymbolic(operator)) continue;

                var operatorIndex = word.indexOf(operator);
                if (operatorIndex == -1) continue;
                if (index != -1 && operatorIndex > index) continue;
                if (operator.length() < length) continue;

                index = operatorIndex;
                length = operator.length();
            }
        }

        return new Pair<>(index, length);
    }

    private static boolean isSymbolic(String string) {
        if (string.length() == 0) return false;

        for (int index = 0; index < string.length(); index++) {
            if (Character.isAlphabetic(string.charAt(index)))
                return false;
        }

        return true;
    }

    private Token[] toTokens() throws InvalidLexemeException {
        var tokens = new ArrayList<Token>();

        for (StringWithLocation word : words) {
            var token = tryResolveToken(word.getString());

            if (token == null)
                throw new InvalidLexemeException(word);

            tokens.add(token);
        }

        var result = new Token[tokens.size()];
        tokens.toArray(result);
        return result;
    }

    private static Token tryResolveToken(String lexeme) {
        Token token;

        token = tryResolveSpecificToken(lexeme, keywords, TokenType.Keyword);
        if (token != null) return token;

        for (Set<String> operators : operatorOrders) {
            token = tryResolveSpecificToken(lexeme, operators, TokenType.Operator);
            if (token != null) return token;
        }

        token = tryResolveSpecificToken(lexeme, declarationSeparators, TokenType.DeclarationSeparator);
        if (token != null) return token;

        token = tryResolveIdentifier(lexeme);
        if (token != null) return token;

        token = tryResolveLiteral(lexeme);
        return token;
    }

    private static Token tryResolveSpecificToken(String lexeme, Set<String> definedLexemes, TokenType type) {
        if (definedLexemes.contains(lexeme))
            return new Token(type, lexeme);

        return null;
    }

    private static Token tryResolveSpecificToken(String lexeme, char[] definedLexemes, TokenType type) {
        if (lexeme.length() != 1) return null;

        for (char definedLexeme : definedLexemes) {
            if (lexeme.charAt(0) == definedLexeme)
                return new Token(type, lexeme);
        }

        return null;
    }

    private static Token tryResolveIdentifier(String lexeme) {
        if (Pattern.matches("^[A-Za-z_][A-Za-z_0-9]*$", lexeme))
            return new Token(TokenType.Identifier, lexeme);

        return null;
    }

    private static Token tryResolveLiteral(String lexeme) {
        if (isIntegerLiteral(lexeme))
            return new Token(TokenType.Literal, lexeme);

        if (isRealLiteral(lexeme))
            return new Token(TokenType.Literal, lexeme);

        return null;
    }

    private static boolean isIntegerLiteral(String str)  {
        try {
            int value = Integer.parseInt(str);
            return value >= 0;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static boolean isRealLiteral(String str) {
        try {
            var value = Float.parseFloat(str);
            return value >= 0f;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static final char[] declarationSeparators = {';', '\n'};

    private static final List<Set<String>> operatorOrders = Arrays.asList(
            new HashSet<>(Arrays.asList("and", "or", "xor",
                    "not", "<", "<=", ">", ">=", "=", "/=", "*", "/", "%",
                    "+", "-", "..", "[", "]", "(", ")", ":=", ":", ",")),
            new HashSet<>(Arrays.asList(".")));

    private static final Set<String> keywords = new HashSet<>(Arrays.asList(
            "var", "is", "type", "record", "end",
            "array", "if", "then", "else", "routine", "size", "true", "false", "for",
            "in", "reverse", "loop", "integer", "real", "boolean", "return", "while"));
}
