package miniPL;

import java.util.List;

public abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement stmt);

        R visitPrintStatement(PrintStatement stmt);

        R visitVarStatement(VarStatement stmt);

        R visitForStatement(ForStatement stmt);

        R visitReadStatement(ReadStatement stmt);

        R visitAssertStatement(AssertStatement stmt);

        R visitAssignStatement(AssignStatement stmt);
    }

    static class AssignStatement extends Statement {
        AssignStatement(Token name, Expression value) {
            this.name = name;
            this.value = value;
        }

        final Token name;
        final Expression value;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignStatement(this);
        }
    }

    static class ExpressionStatement extends Statement {
        ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        final Expression expression;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }

    static class PrintStatement extends Statement {
        PrintStatement(Expression expression) {
            this.expression = expression;
        }

        final Expression expression;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStatement(this);
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
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStatement(this);
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
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStatement(this);
        }
    }

    static class ReadStatement extends Statement {
        ReadStatement(Token ident) {
            this.ident = ident;
        }

        final Token ident;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReadStatement(this);
        }
    }

    static class AssertStatement extends Statement {
        AssertStatement(Expression expr) {
            this.expr = expr;
        }

        final Expression expr;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssertStatement(this);
        }

    }

    abstract <R> R accept(Visitor<R> visitor);
}
