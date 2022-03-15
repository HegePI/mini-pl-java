package miniPL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        if (args.length < 1) {
            System.out.println("You must provide file which is to be interpreted");
            System.exit(64);
        } else {
            System.out.println("File: " + args[0]);
            runFile(args[0]);
        }
    }

    private static void runFile(String file) throws Exception {
        String contents = Files.readString(Path.of(file));
        runInterpreter(contents);
    }

    private static void runInterpreter(String contents) throws Exception {
        Scanner sc = new Scanner(contents);
        sc.scanTokens();
        List<Token> tl = sc.getTokens();

        Parser p = new Parser(tl);
        List<Statement> sl = p.parse();

        Interpreter i = new Interpreter();
        i.interpret(sl);
    }
}
