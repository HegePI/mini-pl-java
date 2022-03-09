package miniPL;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() throws Exception {
        List<Stmt> l = new ArrayList<>();
        while (!isAtEnd()) {
            l.add(declaration());
        }
        return l;
    }

    private Stmt declaration() throws Exception {
        try {
            if (match(TokenType.VAR)) {
                return varDeclaration();
            }
            return statement();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private Stmt varDeclaration() throws Exception {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        consume(TokenType.DDOT, "Expect ':' after variable identifier to define type");

        Token type = null;
        if (match(TokenType.INT)) {
            type = consume(TokenType.INT, "");
        } else if (match(TokenType.STRING)) {
            type = consume(TokenType.STRING, "");
        }

        Expr initializer = null;
        if (match(TokenType.EQ)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, type, initializer);
    }

    private Stmt statement() throws Exception {
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

        return expressionStatement();
    }

    private Stmt assertStatement() throws Exception {
        consume(TokenType.LPAREN, "Expect '(' in assert statement");
        Expr expr = expression();
        consume(TokenType.RPAREN, "Unclosed '(', expect ')'");
        consume(TokenType.SEMICOLON, "Expect ';' after assert statement");
        return new Stmt.Assert(expr);
    }

    private Stmt readStatement() throws Exception {
        Token ident = consume(TokenType.IDENTIFIER, "Expect identifier to store into red input value");

        consume(TokenType.SEMICOLON, "Expect ';' after read statement");
        return new Stmt.Read(ident);
    }

    private Stmt expressionStatement() throws Exception {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt printStatement() throws Exception {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt forStatement() throws Exception {

        Token varIdent = consume(TokenType.IDENTIFIER, "Expect identifier after \"for\" statement");
        consume(TokenType.IN, "Expect \"in\" after variable");
        Expr left = expression();
        consume(TokenType.DOT, "Expect \"..\" to define range");
        consume(TokenType.DOT, "Expect \"..\" to define range");
        Expr right = expression();
        consume(TokenType.DO, "Expect keyword \"for\"");

        List<Stmt> body = new ArrayList<Stmt>();
        while (peek().type != TokenType.END) {
            body.add(statement());
        }

        consume(TokenType.END, "Expect \"end for\" to end for loop");
        consume(TokenType.FOR, "Expect \"end for\" to end for loop");

        return new Stmt.For(varIdent, left, right, body);

    }

    private Expr equality() throws Exception {
        Expr expr = comparison();

        while (match(TokenType.NOTEQ, TokenType.EQ)) {
            Token op = previous();
            Expr r = comparison();
            expr = new Expr.Binary(expr, op, r);
        }
        return expr;
    }

    private Expr comparison() throws Exception {
        Expr expr = term();

        while (match(TokenType.GREATER)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;

    }

    private Expr term() throws Exception {
        Expr expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() throws Exception {
        Expr expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() throws Exception {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() throws Exception {
        if (match(TokenType.FALSE))
            return new Expr.Literal(false);
        if (match(TokenType.TRUE))
            return new Expr.Literal(true);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        if (match(TokenType.STRINGLITERAL)) {
            return new Expr.Literal(previous().literal);
        }
        throw new Exception("WTF");
    }

    private Expr expression() throws Exception {
        return assignment();
    }

    private Expr assignment() throws Exception {
        Expr expr = equality();

        if (match(TokenType.EQ)) {
            // Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
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

}
