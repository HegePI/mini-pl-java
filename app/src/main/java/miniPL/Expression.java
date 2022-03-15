package miniPL;

public abstract class Expression {
    interface Visitor<V> {
        V visitAssignExpr(Assign expr) throws Exception;

        V visitBinaryExpr(Binary expr) throws Exception;

        V visitGroupingExpr(Grouping expr) throws Exception;

        V visitLiteralExpr(Literal expr);

        V visitUnaryExpr(Unary expr) throws Exception;

        V visitVariableExpr(Variable expr) throws Exception;

    }

    static class Assign extends Expression {
        Assign(Token name, Expression value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <V> V accept(Visitor<V> visitor) throws Exception {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expression value;
    }

    static class Binary extends Expression {
        Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitBinaryExpr(this);
        }

        final Expression left;
        final Token operator;
        final Expression right;
    }

    static class Grouping extends Expression {
        Grouping(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitGroupingExpr(this);
        }

        final Expression expression;
    }

    static class Literal extends Expression {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    static class Unary extends Expression {
        Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expression right;
    }

    static class Variable extends Expression {
        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }

    abstract <V> V accept(Visitor<V> visitor) throws Exception;

}
