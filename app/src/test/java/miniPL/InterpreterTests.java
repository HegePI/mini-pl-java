package miniPL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class InterpreterTests {

    @Test
    public void Interprets() throws Exception {
        String content = Files.readString(Path.of("/home/heikki/koulu/compilers/mini-pl-java/test.mpl"));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        Parser p = new Parser(sc.getTokens());

        List<Stmt> l = p.parse();

        Interpreter i = new Interpreter();

        i.interpret(l);
    }

}
