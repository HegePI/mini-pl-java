package miniPL;

import java.util.List;

import miniPL.Expression.BinaryExpression;
import miniPL.Expression.GroupingExpression;
import miniPL.Expression.LiteralExpression;
import miniPL.Expression.UnaryExpression;
import miniPL.Expression.VariableExpression;
import miniPL.Statement.AssertStatement;
import miniPL.Statement.AssignStatement;
import miniPL.Statement.ExpressionStatement;
import miniPL.Statement.ForStatement;
import miniPL.Statement.PrintStatement;
import miniPL.Statement.ReadStatement;
import miniPL.Statement.VarStatement;
import java.util.Scanner;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {

    public final Environment env = new Environment();

    public final Scanner sc = new Scanner(System.in);

    public void interpret(List<Statement> statements) throws Exception {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (Exception error) {
            throw new Exception(error);
        }
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) throws Exception {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                return (int) left - (int) right;
            case PLUS:
                if (left instanceof Integer && right instanceof Integer) {
                    return (int) left + (int) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                break;
            case SLASH:
                return (int) left / (int) right;
            case STAR:
                return (int) left * (int) right;
            case GREATER:
                return (int) left < (int) right;
            case NOTEQ:
                return !isEqual(left, right);
            case EQ:
                return isEqual(left, right);
        }
        return null;
    }

    @Override
    public Object visitGroupingExpression(GroupingExpression expr) throws Exception {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpression(LiteralExpression expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression expr) throws Exception {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                return -(int) right;
            case BANG:
                return !isTruthy(right);
        }
        return null;
    }

    @Override
    public Object visitVariableExpression(VariableExpression expr) throws Exception {
        return env.get(expr.name);

    }

    @Override
    public Void visitExpressionStmt(ExpressionStatement stmt) throws Exception {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStatement stmt) throws Exception {
        Object value = evaluate(stmt.expression);
        System.out.print(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(VarStatement stmt) throws Exception {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        env.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitForStmt(ForStatement stmt) throws Exception {

        int value = (int) evaluate(stmt.left);
        env.define(stmt.varIdent.lexeme, value);

        while (isInRange(stmt)) {
            for (Statement s : stmt.body) {
                execute(s);
            }
            int lastValue = (int) env.get(stmt.varIdent);
            env.define(stmt.varIdent.lexeme, lastValue + 1);
        }
        return null;
    }

    @Override
    public Void visitReadStmt(ReadStatement stmt) throws Exception {

        String input = sc.nextLine();

        try {
            int intInput = Integer.parseInt(input);
            env.define(stmt.ident.lexeme, intInput);
        } catch (Exception e) {
            env.define(stmt.ident.lexeme, input);
        }
        return null;
    }

    @Override
    public Void visitAssertStmt(AssertStatement stmt) throws Exception {
        if (isTruthy(evaluate(stmt.expr))) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
        return null;
    }

    @Override
    public Void visitAssignStatement(AssignStatement stmt) throws Exception {
        env.define(stmt.name.lexeme, evaluate(stmt.value));
        return null;
    }

    private void execute(Statement statement) throws Exception {
        statement.accept(this);
    }

    private Object evaluate(Expression expr) throws Exception {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    private boolean isInRange(Statement.ForStatement stmt) throws Exception {
        int value = (int) env.get(stmt.varIdent);
        int r = (int) evaluate(stmt.right);
        return value < r;
    }

    private String stringify(Object object) {
        if (object == null) {
            return "null";
        }
        return object.toString();
    }
}
