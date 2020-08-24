package projectI;

import org.javatuples.Pair;

import java.util.*;
import java.util.regex.Pattern;

public class Lexer {
    public Token[] scan(String programText) throws InvalidLexemeException {
        programText = changeNewLinesToConventional(programText);
        var words = new ArrayList<>(Arrays.asList(splitToWords(programText)));
        separateDeclarations(words);

        for (int order = 0; order < operatorOrders.size(); order++) {
            separateSymbolicOperators(words, order);
        }

        return toTokens(words);
    }

    private static String changeNewLinesToConventional(String programText) {
        return programText.replaceAll("\r\n", "\n");
    }

    private String[] splitToWords(String programText) {
        return programText.split(" ");
    }

    private void separateDeclarations(List<String> words) {
        for (int index = 0; index < words.size(); index++) {
            var word = words.get(index);
            var separatorIndex = getIndexOfDeclarationSeparator(word);
            if (separatorIndex == -1 || word.length() == 1) continue;

            var left = word.substring(0, separatorIndex);
            var separator = word.charAt(separatorIndex);
            var right = word.substring(separatorIndex + 1);

            words.set(index, left);
            words.add(index + 1, Character.toString(separator));
            words.add(index + 2, right);
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

    private static void removeBlank(List<String> strings) {
        for (int index = strings.size() - 1; index >= 0; index--) {
            if (strings.get(index).isEmpty())
                strings.remove(index);
        }
    }

    private static void separateSymbolicOperators(List<String> words, int order) {
        for (int index = 0; index < words.size(); index++) {
            var word = words.get(index);
            if (isIntegerLiteral(word) || isRealLiteral(word)) continue;

            var position = getPositionOfSymbolicOperator(word, order);
            var operatorIndex = position.getValue0();
            var operatorLength = position.getValue1();
            if (operatorIndex == -1 || operatorLength == word.length()) continue;

            var left = word.substring(0, operatorIndex);
            var operator = word.substring(operatorIndex, operatorIndex + operatorLength);
            var right = word.substring(operatorIndex + operatorLength);

            words.set(index, left);
            words.add(index + 1, operator);
            words.add(index + 2, right);
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

    private static Token[] toTokens(List<String> words) throws InvalidLexemeException {
        var tokens = new ArrayList<Token>();

        for (String word : words) {
            var token = tryResolveToken(word);

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
