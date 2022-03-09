package miniPL;

import java.util.List;

public abstract class Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt) throws Exception;

        R visitPrintStmt(Print stmt) throws Exception;

        R visitVarStmt(Var stmt) throws Exception;

        R visitForStmt(For stmt) throws Exception;

        R visitReadStmt(Read stmt) throws Exception;

        R visitAssertStmt(Assert stmt) throws Exception;
    }

    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Print extends Stmt {
        Print(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Var extends Stmt {
        Var(Token name, Token type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        final Token name;
        final Token type;
        final Expr initializer;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitVarStmt(this);
        }
    }

    static class For extends Stmt {
        For(Expr varIdent, Expr left, Expr right, List<Stmt> body) {
            this.varIdent = varIdent;
            this.left = left;
            this.right = right;
            this.body = body;
        }

        final Expr varIdent;
        final Expr left;
        final Expr right;
        final List<Stmt> body;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitForStmt(this);
        }
    }

    static class Read extends Stmt {
        Read(Token ident) {
            this.ident = ident;
        }

        final Token ident;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitReadStmt(this);
        }
    }

    static class Assert extends Stmt {
        Assert(Expr expr) {
            this.expr = expr;
        }

        final Expr expr;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitAssertStmt(this);
        }

    }

    abstract <R> R accept(Visitor<R> visitor) throws Exception;
}
