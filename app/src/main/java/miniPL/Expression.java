package miniPL;

public abstract class Expression {
    interface Visitor<V> {

        V visitBinaryExpression(BinaryExpression expr);

        V visitGroupingExpression(GroupingExpression expr);

        V visitLiteralExpression(LiteralExpression expr);

        V visitUnaryExpression(UnaryExpression expr);

        V visitVariableExpression(VariableExpression expr);

    }

    static class BinaryExpression extends Expression {
        BinaryExpression(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }

        final Expression left;
        final Token operator;
        final Expression right;
    }

    static class GroupingExpression extends Expression {
        GroupingExpression(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }

        final Expression expression;
    }

    static class LiteralExpression extends Expression {
        LiteralExpression(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }

        final Object value;
    }

    static class UnaryExpression extends Expression {
        UnaryExpression(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }

        final Token operator;
        final Expression right;
    }

    static class VariableExpression extends Expression {
        VariableExpression(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpression(this);
        }

        final Token name;
    }

    abstract <V> V accept(Visitor<V> visitor);

}
