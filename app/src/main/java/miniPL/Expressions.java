package miniPL;

public abstract class Expressions {
    interface Visitor<V> {
        V visitAssignExpr(Assign expr) throws Exception;

        V visitBinaryExpr(Binary expr) throws Exception;

        V visitGroupingExpr(Grouping expr) throws Exception;

        V visitLiteralExpr(Literal expr);

        V visitUnaryExpr(Unary expr) throws Exception;

        V visitVariableExpr(Variable expr) throws Exception;

    }

    static class Assign extends Expressions {
        Assign(Token name, Expressions value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <V> V accept(Visitor<V> visitor) throws Exception {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expressions value;
    }

    static class Binary extends Expressions {
        Binary(Expressions left, Token operator, Expressions right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitBinaryExpr(this);
        }

        final Expressions left;
        final Token operator;
        final Expressions right;
    }

    static class Grouping extends Expressions {
        Grouping(Expressions expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitGroupingExpr(this);
        }

        final Expressions expression;
    }

    static class Literal extends Expressions {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    static class Unary extends Expressions {
        Unary(Token operator, Expressions right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) throws Exception {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expressions right;
    }

    static class Variable extends Expressions {
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
