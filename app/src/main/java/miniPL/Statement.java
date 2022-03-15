package miniPL;

import java.util.List;

public abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStmt(ExpressionStatement stmt) throws Exception;

        R visitPrintStmt(PrintStatement stmt) throws Exception;

        R visitVarStmt(VarStatement stmt) throws Exception;

        R visitForStmt(ForStatement stmt) throws Exception;

        R visitReadStmt(ReadStatement stmt) throws Exception;

        R visitAssertStmt(AssertStatement stmt) throws Exception;
    }

    static class ExpressionStatement extends Statement {
        ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        final Expression expression;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class PrintStatement extends Statement {
        PrintStatement(Expression expression) {
            this.expression = expression;
        }

        final Expression expression;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitPrintStmt(this);
        }
    }

    static class VarStatement extends Statement {
        VarStatement(Token name, Token type, Expression initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        final Token name;
        final Token type;
        final Expression initializer;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitVarStmt(this);
        }
    }

    static class ForStatement extends Statement {
        ForStatement(Token varIdent, Expression left, Expression right, List<Statement> body) {
            this.varIdent = varIdent;
            this.left = left;
            this.right = right;
            this.body = body;
        }

        final Token varIdent;
        final Expression left;
        final Expression right;
        final List<Statement> body;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitForStmt(this);
        }
    }

    static class ReadStatement extends Statement {
        ReadStatement(Token ident) {
            this.ident = ident;
        }

        final Token ident;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitReadStmt(this);
        }
    }

    static class AssertStatement extends Statement {
        AssertStatement(Expression expr) {
            this.expr = expr;
        }

        final Expression expr;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitAssertStmt(this);
        }

    }

    abstract <R> R accept(Visitor<R> visitor) throws Exception;
}
