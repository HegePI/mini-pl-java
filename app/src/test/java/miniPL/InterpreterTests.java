package miniPL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class InterpreterTests {

    String TEST_FILE = "/home/heikki/koulu/compilers/mini-pl-java/interpreting.test.mpl";
    String ASSERT_TEST_FILE = "/home/heikki/koulu/compilers/mini-pl-java/assert-interpreting.test.mpl";

    @Test
    public void interprets() throws Exception {
        String content = Files.readString(Path.of(TEST_FILE));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        Parser p = new Parser(sc.getTokens());

        List<Statements> l = p.parse();

        Interpreter i = new Interpreter();

        i.interpret(l);
    }

    @Test
    public void assertInterpretetation() throws Exception {
        String content = Files.readString(Path.of(TEST_FILE));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        Parser p = new Parser(sc.getTokens());

        List<Statements> l = p.parse();

        Interpreter i = new Interpreter();

        i.interpret(l);
    }

}
