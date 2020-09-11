package projectI;

import projectI.Lexer.InvalidLexemeException;
import projectI.Lexer.Lexer;
import projectI.Parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws IOException, InvalidLexemeException {
        String fileName = getFileName(args);

        var file = new File(fileName);

        if (file.exists()) {
            try {
                var sourceCode = Files.readString(file.toPath());
                var parser = createParserOf(sourceCode);
                var program = parser.tryParseProgram();

                if (program != null && program.validate()) {
                    System.out.println(program);
                } else {
                    printParsingErrors(parser);
                }
            } catch (IOException e) {
                System.err.println("An error occurred when trying to read the file.");
                throw e;
            } catch (InvalidLexemeException e) {
                System.err.println("Lexical analyzer reported an error.");
                throw e;
            }
        } else {
            System.err.println("File does not exist.");
        }
    }

    private static String getFileName(String[] args) {
        String fileName;

        if (args.length == 1) {
            fileName = args[0];
        } else {
            System.out.print("Enter file name: ");
            var scanner = new Scanner(System.in);
            fileName = scanner.nextLine();
        }

        return fileName;
    }

    private static Parser createParserOf(String sourceCode) throws InvalidLexemeException {
        var lexer = new Lexer();
        var tokens = lexer.scan(sourceCode);
        return new Parser(tokens, lexer.getLexemesWithLocations());
    }

    private static void printParsingErrors(Parser parser) {
        System.err.printf("Parser reported %d error(s).\n",parser.getErrorCount());

        for (var error: parser.getErrors()) {
            System.err.println(error);
        }
    }
}
