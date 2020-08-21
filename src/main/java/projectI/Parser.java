import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import org.javatuples.Pair;

public class Parser {
    final String[] keywords = {"var", "is", "type", "record", "end",
           "array", "if", "then", "else", "routine",
           "length", "size"};

    final String[] operators = {"and", "or", "xor",
        "not", "<", "<=", ">", "=", "/=", "*", "/", "%",
        "+", "-", ".."};

    final String[] symbols = {"[", "]", "."};

    public enum TokenKind {
        Keyword,
        Identifier,
        Separator,
        Operator,
        Type
    }

    public static String readString(String str) throws IOException {
        Path fileName = Path.of(str);
        return Files.readString(fileName);
    }

    public static LinkedList<Pair<String,Character>> splitToDeclarations(String str){
        LinkedList<Pair<String,Character>> finalList = new LinkedList();
        LinkedList<Character> strBuffer = new LinkedList();
        Pair<String, Character> pair = new Pair<String, Character>("",'\n');

        for(char ch : str.toCharArray()){
            switch (ch) {
            case ';':
                pair.setAt0(strBuffer.toString());
                pair.setAt1(ch);
                finalList.add(pair);
                strBuffer.clear();
                break;
            case '\n':
                pair.setAt0(strBuffer.toString());
                pair.setAt1(ch);
                finalList.add(pair);
                strBuffer.clear();
                break;
            default:
                strBuffer.add(ch);
            }
        }

        pair.setAt0(strBuffer.toString());
        pair.setAt1('\n');
        finalList.add(pair);

        return finalList;
    }
}
