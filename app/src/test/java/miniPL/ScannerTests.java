package miniPL;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ScannerTests {

    String TEST_FILE = "/home/heikki/koulu/compilers/mini-pl-java/scanning.test.mpl";

    @Test
    public void scansTokens() throws Exception {
        String content = Files.readString(Path.of(TEST_FILE));

        Scanner sc = new Scanner(content);

        sc.scanTokens();

        List<Token> tokens = sc.getTokens();

        assertEquals(TokenType.VAR, tokens.get(0).getTokenType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getTokenType());
        assertEquals(TokenType.DDOT, tokens.get(2).getTokenType());
        assertEquals(TokenType.STRING, tokens.get(3).getTokenType());
        assertEquals(TokenType.ASSIGN, tokens.get(4).getTokenType());
        assertEquals(TokenType.STRINGLITERAL, tokens.get(5).getTokenType());
        assertEquals(TokenType.SEMICOLON, tokens.get(6).getTokenType());
    }
}
