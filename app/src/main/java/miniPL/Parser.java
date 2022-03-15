package miniPL;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() throws Exception {
        List<Statement> l = new ArrayList<>();
        while (!isAtEnd()) {
            l.add(statement());
        }
        return l;
    }

    private Statement statement() throws Exception {
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

    private Statement varStatement() throws Exception {
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

        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.VarStatement(name, type, initializer);
    }

    private Statement assertStatement() throws Exception {
        consume(TokenType.LPAREN, "Expect '(' in assert statement");
        Expression expr = expression();
        consume(TokenType.RPAREN, "Unclosed '(', expect ')'");
        consume(TokenType.SEMICOLON, "Expect ';' after assert statement");
        return new Statement.AssertStatement(expr);
    }

    private Statement readStatement() throws Exception {
        Token ident = consume(TokenType.IDENTIFIER, "Expect identifier to store into red input value");

        consume(TokenType.SEMICOLON, "Expect ';' after read statement");
        return new Statement.ReadStatement(ident);
    }

    private Statement printStatement() throws Exception {
        Expression value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Statement.PrintStatement(value);
    }

    private Statement forStatement() throws Exception {

        Token varIdent = consume(TokenType.IDENTIFIER, "Expect identifier after \"for\" statement");
        consume(TokenType.IN, "Expect \"in\" after variable");
        Expression left = expression();
        consume(TokenType.DOT, "Expect \"..\" to define range");
        consume(TokenType.DOT, "Expect \"..\" to define range");
        Expression right = expression();
        consume(TokenType.DO, "Expect keyword \"for\"");

        List<Statement> body = new ArrayList<Statement>();
        while (peek().type != TokenType.END) {
            body.add(statement());
        }

        consume(TokenType.END, "Expect \"end for\" to end for loop");
        consume(TokenType.FOR, "Expect \"end for\" to end for loop");
        consume(TokenType.SEMICOLON, "Expect ';' after statement");

        return new Statement.ForStatement(varIdent, left, right, body);

    }

    private Statement expressionStatement() throws Exception {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Statement.ExpressionStatement(expr);
    }

    private Expression equality() throws Exception {
        Expression expr = comparison();

        while (match(TokenType.NOTEQ, TokenType.EQ)) {
            Token op = previous();
            Expression r = comparison();
            expr = new Expression.Binary(expr, op, r);
        }
        return expr;
    }

    private Expression comparison() throws Exception {
        Expression expr = term();

        while (match(TokenType.GREATER)) {
            Token operator = previous();
            Expression right = term();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;

    }

    private Expression term() throws Exception {
        Expression expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() throws Exception {
        Expression expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() throws Exception {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    private Expression primary() throws Exception {
        if (match(TokenType.FALSE))
            return new Expression.Literal(false);
        if (match(TokenType.TRUE))
            return new Expression.Literal(true);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(previous().literal);
        }

        if (match(TokenType.LPAREN)) {
            Expression expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expr);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expression.Variable(previous());
        }
        if (match(TokenType.STRINGLITERAL)) {
            return new Expression.Literal(previous().literal);
        }
        throw new Exception("WTF");
    }

    private Expression expression() throws Exception {
        return assignment();
    }

    private Expression assignment() throws Exception {
        Expression expr = equality();

        if (match(TokenType.EQ)) {
            Expression value = assignment();
            if (expr instanceof Expression.Variable) {
                Token name = ((Expression.Variable) expr).name;
                return new Expression.Assign(name, value);
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
