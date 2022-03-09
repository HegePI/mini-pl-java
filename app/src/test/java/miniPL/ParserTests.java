package miniPL;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class ParserTests {

    String TEST_FILE = "/home/heikki/koulu/compilers/mini-pl-java/parsing.test.mpl";

    @Test
    public void readsTokens() throws Exception {
        String content = Files.readString(Path.of(TEST_FILE));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        Parser p = new Parser(sc.getTokens());

        p.parse();
    }

}
