package miniPL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public void scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = readChar();
        switch (c) {
            case '(':
                addToken(TokenType.LPAREN);
                break;
            case ')':
                addToken(TokenType.RPAREN);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        readChar();
                    break;
                } else {
                    addToken(TokenType.SLASH);
                    break;
                }
            case '*':
                addToken(TokenType.STAR);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case ':':
                if (match('=')) {
                    addToken(TokenType.ASSIGN);
                    break;
                } else {
                    addToken(TokenType.DDOT);
                    break;
                }
            case '=':
                addToken(TokenType.EQ);
                break;
            case '<':
                addToken(TokenType.GREATER);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '!':
                if (match('=')) {
                    addToken(TokenType.NOTEQ);
                    break;
                } else {
                    addToken(TokenType.BANG);
                    break;
                }
            case '"':
                readStringLiteral();
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    readNumberLiteral();
                } else if (isAlpha(c)) {
                    readIdentifier();
                } else {
                    printScanningError(this.line, "unexpected character");
                }
        }
    }

    private char readChar() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char c) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != c) {
            return false;
        }
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private void readStringLiteral() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            readChar();
        }

        if (isAtEnd()) {
            printScanningError(this.line, "encountered unclosed string literal");
        }
        readChar();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRINGLITERAL, value);

    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void readNumberLiteral() {
        while (isDigit(peek()))
            readChar();

        if (peek() == '.' && isDigit(peekNext())) {
            readChar();

            while (isDigit(peek()))
                readChar();
        }

        addToken(TokenType.NUMBER,
                Integer.parseInt(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private void readIdentifier() {
        while (isAlphaNumeric(peek())) {
            readChar();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = TokenType.IDENTIFIER;
        addToken(type);

    }

    private void printScanningError(int line2, String msg) {
        System.err.println(String.format("Scanning error on line: " + line2 + "\n" + msg));
        System.exit(1);
    }

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("for", TokenType.FOR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("var", TokenType.VAR);
        keywords.put("end", TokenType.END);
        keywords.put("in", TokenType.IN);
        keywords.put("do", TokenType.DO);
        keywords.put("read", TokenType.READ);
        keywords.put("int", TokenType.INT);
        keywords.put("string", TokenType.STRING);
        keywords.put("bool", TokenType.BOOL);
        keywords.put("assert", TokenType.ASSERT);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
    }

}
