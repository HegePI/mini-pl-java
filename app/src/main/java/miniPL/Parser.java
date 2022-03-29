package miniPL;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        List<Statement> l = new ArrayList<>();
        while (!isAtEnd()) {
            l.add(statement());
        }
        return l;
    }

    private Statement statement() {
        if (match(TokenType.VAR)) {
            return varStatement();
        }
        if (match(TokenType.IDENTIFIER)) {
            return assignStatement();
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }
        if (match(TokenType.READ)) {
            return readStatement();
        }
        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        if (match(TokenType.ASSERT)) {
            return assertStatement();
        }
        return expressionStatement();
    }

    private Statement assignStatement() {
        Token name = previous();
        consume(TokenType.ASSIGN, "Expect ':=' after identifier");
        Expression value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after statement");

        return new Statement.AssignStatement(name, value);
    }

    private Statement varStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name");
        consume(TokenType.DDOT, "Expect ':' after variable identifier to define type");

        Token type = null;
        if (getCurrentToken().type == TokenType.INT) {
            type = consume(TokenType.INT, "");
        } else if (getCurrentToken().type == TokenType.STRING) {
            type = consume(TokenType.STRING, "");
        } else if (getCurrentToken().type == TokenType.BOOL) {
            type = consume(TokenType.BOOL, "");
        }

        Expression initialValue = null;
        if (match(TokenType.ASSIGN)) {
            initialValue = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");

        return new Statement.VarStatement(name, type, initialValue);
    }

    private Statement assertStatement() {
        consume(TokenType.LPAREN, "Expect '(' in assert statement");
        Expression expr = expression();
        consume(TokenType.RPAREN, "Unclosed '(', expect ')'");
        consume(TokenType.SEMICOLON, "Expect ';' after assert statement");

        return new Statement.AssertStatement(expr);
    }

    private Statement readStatement() {
        Token ident = consume(TokenType.IDENTIFIER, "Expect identifier to store into red input value");
        consume(TokenType.SEMICOLON, "Expect ';' after read statement");

        return new Statement.ReadStatement(ident);
    }

    private Statement printStatement() {
        Expression value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");

        return new Statement.PrintStatement(value);
    }

    private Statement forStatement() {
        Token varIdent = consume(TokenType.IDENTIFIER, "Expect identifier after \"for\" statement");
        consume(TokenType.IN, "Expect \"in\" after variable");
        Expression left = expression();
        consume(TokenType.DOT, "Expect \"..\" to define range");
        consume(TokenType.DOT, "Expect \"..\" to define range");
        Expression right = expression();
        consume(TokenType.DO, "Expect keyword \"do\"");

        List<Statement> body = new ArrayList<Statement>();
        while (getCurrentToken().type != TokenType.END) {
            body.add(statement());
        }

        consume(TokenType.END, "Expect \"end for\" to end for loop");
        consume(TokenType.FOR, "Expect \"end for\" to end for loop");
        consume(TokenType.SEMICOLON, "Expect ';' after statement");

        return new Statement.ForStatement(varIdent, left, right, body);

    }

    private Statement expressionStatement() {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");

        return new Statement.ExpressionStatement(expr);
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression left = comparison();
        while (match(TokenType.NOTEQ, TokenType.EQ, TokenType.AND)) {
            Token op = previous();
            Expression right = comparison();
            left = new Expression.BinaryExpression(left, op, right);
        }

        return left;
    }

    private Expression comparison() {
        Expression left = term();
        while (match(TokenType.GREATER)) {
            Token op = readToken();
            Expression right = term();
            left = new Expression.BinaryExpression(left, op, right);
        }

        return left;
    }

    private Expression term() {
        Expression left = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token op = previous();
            Expression right = factor();
            left = new Expression.BinaryExpression(left, op, right);
        }

        return left;
    }

    private Expression factor() {
        Expression left = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token op = readToken();
            Expression right = unary();
            left = new Expression.BinaryExpression(left, op, right);
        }

        return left;
    }

    private Expression unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = readToken();
            Expression right = unary();
            return new Expression.UnaryExpression(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if (match(TokenType.FALSE))
            return new Expression.LiteralExpression(false);
        if (match(TokenType.TRUE))
            return new Expression.LiteralExpression(true);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.LiteralExpression(previous().literal);
        }
        if (match(TokenType.STRINGLITERAL)) {
            return new Expression.LiteralExpression(previous().literal);
        }
        if (match(TokenType.LPAREN)) {
            Expression expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return new Expression.GroupingExpression(expr);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expression.VariableExpression(previous());
        }
        printParsingError("unknown token in primary", getCurrentToken().line);
        return null;
    }

    /**
     * Function, which checks, whether current token is same type as TokenType
     * argument and returns the token if matches. Otherwise prints an error.
     * 
     * @param type    type argument
     * @param message error message
     * @return current token
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return readToken();
        }

        printParsingError(message, getCurrentToken().line);
        return null;
    }

    /**
     * Function that checks whether the current token is any of the provided
     * TokenTypes in the list. If true, reads token, moves current to next token and
     * return true. Otherwise returns false.
     * 
     * @param list types to be checked
     * @return true if current tokne any of the types, otherwise false.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                readToken();
                return true;
            }
        }

        return false;
    }

    /**
     * Returns previous token
     * 
     * @return token at current-1
     */
    private Token previous() {
        return this.tokens.get(current - 1);
    }

    /**
     * reads current token, returns it and increases current by one.
     * 
     * @return token at current
     */
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

        return getCurrentToken().type == type;
    }

    private boolean isAtEnd() {
        return getCurrentToken().type == TokenType.EOF;
    }

    /**
     * returns token at current but doesn't increase current.
     * 
     * @return token at current
     */
    private Token getCurrentToken() {
        return tokens.get(current);
    }

    private void printParsingError(String msg, int line) {
        System.err.println(String.format("Parsing error on line " + line + " : " + msg));
        System.exit(1);
    }
}
