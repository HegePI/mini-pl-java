package miniPL;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class ParserTests {

    @Test
    public void readsTokens() throws Exception {
        String content = Files.readString(Path.of("/home/heikki/koulu/compilers/mini-pl-java/test.mpl"));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        Parser p = new Parser(sc.getTokens());

        p.parse();
    }

}
