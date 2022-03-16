package miniPL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        if (args.length < 1) {
            System.out.println("You must provide file which is to be interpreted");
            System.exit(1);
        } else {
            System.out.println("File: " + args[0]);
            runFile(args[0]);
        }
    }

    private static void runFile(String file) {
        String contents = null;
        try {
            contents = Files.readString(Path.of(file));
        } catch (IOException e) {
            printError("File " + file + " not found");
        }
        runInterpreter(contents);
    }

    private static void runInterpreter(String contents) {
        Scanner sc = new Scanner(contents);
        sc.scanTokens();
        List<Token> tl = sc.getTokens();

        Parser p = new Parser(tl);
        List<Statement> sl = p.parse();

        Interpreter i = new Interpreter();
        i.interpret(sl);
    }

    private static void printError(String msg) {
        System.out.println(String.format("Error: " + msg));
        System.exit(1);
    }
}
