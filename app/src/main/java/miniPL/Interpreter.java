package miniPL;

import java.util.List;

import miniPL.Expressions.Assign;
import miniPL.Expressions.Binary;
import miniPL.Expressions.Grouping;
import miniPL.Expressions.Literal;
import miniPL.Expressions.Unary;
import miniPL.Expressions.Variable;
import miniPL.Statements.Assert;
import miniPL.Statements.Expression;
import miniPL.Statements.For;
import miniPL.Statements.Print;
import miniPL.Statements.Read;
import miniPL.Statements.Var;
import java.util.Scanner;

public class Interpreter implements Expressions.Visitor<Object>, Statements.Visitor<Void> {

    public final Environment env = new Environment();

    public final Scanner sc = new Scanner(System.in);

    public void interpret(List<Statements> statements) throws Exception {
        try {
            for (Statements statement : statements) {
                execute(statement);
            }
        } catch (Exception error) {
            throw new Exception(error);
        }
    }

    @Override
    public Object visitAssignExpr(Assign expr) throws Exception {
        Object value = evaluate(expr.value);
        env.assign(expr.name, value);
        return value;

    }

    @Override
    public Object visitBinaryExpr(Binary expr) throws Exception {
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
    public Object visitGroupingExpr(Grouping expr) throws Exception {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) throws Exception {
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
    public Object visitVariableExpr(Variable expr) throws Exception {
        return env.get(expr.name);

    }

    @Override
    public Void visitExpressionStmt(Expression stmt) throws Exception {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) throws Exception {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) throws Exception {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        env.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) throws Exception {

        int value = (int) evaluate(stmt.left);
        env.define(stmt.varIdent.lexeme, value);

        while (isInRange(stmt)) {
            for (Statements s : stmt.body) {
                execute(s);
            }
            int lastValue = (int) env.get(stmt.varIdent);
            env.define(stmt.varIdent.lexeme, lastValue + 1);
        }
        return null;
    }

    @Override
    public Void visitReadStmt(Read stmt) throws Exception {
        System.out.print("Write an input: ");

        Object input = sc.nextLine();

        if (input instanceof Integer) {
            env.define(stmt.ident.lexeme, (int) input);
        } else if (input instanceof String) {
            env.define(stmt.ident.lexeme, (String) input);
        } else {
            throw new Exception("you can only input string or int literals");
        }
        return null;
    }

    @Override
    public Void visitAssertStmt(Assert stmt) throws Exception {
        if (isTruthy(evaluate(stmt.expr))) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
        return null;
    }

    private void execute(Statements statement) throws Exception {
        statement.accept(this);
    }

    private Object evaluate(Expressions expr) throws Exception {
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

    private boolean isInRange(Statements.For stmt) throws Exception {
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
