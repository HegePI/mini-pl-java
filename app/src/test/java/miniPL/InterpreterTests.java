package miniPL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class InterpreterTests {

    String TEST_FILE = "/home/heikki/koulu/compilers/mini-pl-java/interpreting.test.mpl";

    @Test
    public void Interprets() throws Exception {
        String content = Files.readString(Path.of(TEST_FILE));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        Parser p = new Parser(sc.getTokens());

        List<Stmt> l = p.parse();

        Interpreter i = new Interpreter();

        i.interpret(l);
    }

}
