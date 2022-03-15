package miniPL;

import java.util.List;

public abstract class Statements {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt) throws Exception;

        R visitPrintStmt(Print stmt) throws Exception;

        R visitVarStmt(Var stmt) throws Exception;

        R visitForStmt(For stmt) throws Exception;

        R visitReadStmt(Read stmt) throws Exception;

        R visitAssertStmt(Assert stmt) throws Exception;
    }

    static class Expression extends Statements {
        Expression(Expressions expression) {
            this.expression = expression;
        }

        final Expressions expression;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Print extends Statements {
        Print(Expressions expression) {
            this.expression = expression;
        }

        final Expressions expression;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Var extends Statements {
        Var(Token name, Token type, Expressions initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        final Token name;
        final Token type;
        final Expressions initializer;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitVarStmt(this);
        }
    }

    static class For extends Statements {
        For(Token varIdent, Expressions left, Expressions right, List<Statements> body) {
            this.varIdent = varIdent;
            this.left = left;
            this.right = right;
            this.body = body;
        }

        final Token varIdent;
        final Expressions left;
        final Expressions right;
        final List<Statements> body;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitForStmt(this);
        }
    }

    static class Read extends Statements {
        Read(Token ident) {
            this.ident = ident;
        }

        final Token ident;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitReadStmt(this);
        }
    }

    static class Assert extends Statements {
        Assert(Expressions expr) {
            this.expr = expr;
        }

        final Expressions expr;

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitAssertStmt(this);
        }

    }

    abstract <R> R accept(Visitor<R> visitor) throws Exception;
}
