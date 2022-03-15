package miniPL;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statements> parse() throws Exception {
        List<Statements> l = new ArrayList<>();
        while (!isAtEnd()) {
            l.add(statement());
        }
        return l;
    }

    private Statements statement() throws Exception {
        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }
        if (match(TokenType.READ)) {
            return readStatement();
        }
        if (match(TokenType.ASSERT)) {
            return assertStatement();
        }
        if (match(TokenType.VAR)) {
            return varStatement();
        }

        return expressionStatement();
    }

    private Statements varStatement() throws Exception {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        consume(TokenType.DDOT, "Expect ':' after variable identifier to define type");

        Token type = null;
        if (peek().type == TokenType.INT) {
            type = consume(TokenType.INT, "");
        } else if (peek().type == TokenType.STRING) {
            type = consume(TokenType.STRING, "");
        } else if (peek().type == TokenType.BOOL) {
            type = consume(TokenType.BOOL, "");
        }

        Expressions initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Statements.Var(name, type, initializer);
    }

    private Statements assertStatement() throws Exception {
        consume(TokenType.LPAREN, "Expect '(' in assert statement");
        Expressions expr = expression();
        consume(TokenType.RPAREN, "Unclosed '(', expect ')'");
        consume(TokenType.SEMICOLON, "Expect ';' after assert statement");
        return new Statements.Assert(expr);
    }

    private Statements readStatement() throws Exception {
        Token ident = consume(TokenType.IDENTIFIER, "Expect identifier to store into red input value");

        consume(TokenType.SEMICOLON, "Expect ';' after read statement");
        return new Statements.Read(ident);
    }

    private Statements printStatement() throws Exception {
        Expressions value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Statements.Print(value);
    }

    private Statements forStatement() throws Exception {

        Token varIdent = consume(TokenType.IDENTIFIER, "Expect identifier after \"for\" statement");
        consume(TokenType.IN, "Expect \"in\" after variable");
        Expressions left = expression();
        consume(TokenType.DOT, "Expect \"..\" to define range");
        consume(TokenType.DOT, "Expect \"..\" to define range");
        Expressions right = expression();
        consume(TokenType.DO, "Expect keyword \"for\"");

        List<Statements> body = new ArrayList<Statements>();
        while (peek().type != TokenType.END) {
            body.add(statement());
        }

        consume(TokenType.END, "Expect \"end for\" to end for loop");
        consume(TokenType.FOR, "Expect \"end for\" to end for loop");
        consume(TokenType.SEMICOLON, "Expect ';' after statement");

        return new Statements.For(varIdent, left, right, body);

    }

    private Statements expressionStatement() throws Exception {
        Expressions expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Statements.Expression(expr);
    }

    private Expressions equality() throws Exception {
        Expressions expr = comparison();

        while (match(TokenType.NOTEQ, TokenType.EQ)) {
            Token op = previous();
            Expressions r = comparison();
            expr = new Expressions.Binary(expr, op, r);
        }
        return expr;
    }

    private Expressions comparison() throws Exception {
        Expressions expr = term();

        while (match(TokenType.GREATER)) {
            Token operator = previous();
            Expressions right = term();
            expr = new Expressions.Binary(expr, operator, right);
        }

        return expr;

    }

    private Expressions term() throws Exception {
        Expressions expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expressions right = factor();
            expr = new Expressions.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expressions factor() throws Exception {
        Expressions expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expressions right = unary();
            expr = new Expressions.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expressions unary() throws Exception {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expressions right = unary();
            return new Expressions.Unary(operator, right);
        }

        return primary();
    }

    private Expressions primary() throws Exception {
        if (match(TokenType.FALSE))
            return new Expressions.Literal(false);
        if (match(TokenType.TRUE))
            return new Expressions.Literal(true);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expressions.Literal(previous().literal);
        }

        if (match(TokenType.LPAREN)) {
            Expressions expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return new Expressions.Grouping(expr);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expressions.Variable(previous());
        }
        if (match(TokenType.STRINGLITERAL)) {
            return new Expressions.Literal(previous().literal);
        }
        throw new Exception("WTF");
    }

    private Expressions expression() throws Exception {
        return assignment();
    }

    private Expressions assignment() throws Exception {
        Expressions expr = equality();

        if (match(TokenType.EQ)) {
            Expressions value = assignment();
            if (expr instanceof Expressions.Variable) {
                Token name = ((Expressions.Variable) expr).name;
                return new Expressions.Assign(name, value);
            }
            throw new Exception("Invalid asignment type");
        }
        return expr;
    }

    private Token consume(TokenType type, String message) throws Exception {
        if (check(type)) {
            return readToken();
        }

        throw new Exception(message);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                readToken();
                return true;
            }
        }
        return false;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token readToken() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean checkTypes(Token... tokens) throws Exception {
        return false;
    }

    private void printParsingError(String msg) {
        System.err.println("Error: " + msg);
        System.exit(64);
    }
}
