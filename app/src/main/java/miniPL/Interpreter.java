package miniPL;

import java.util.List;

import miniPL.Expr.Assign;
import miniPL.Expr.Binary;
import miniPL.Expr.Grouping;
import miniPL.Expr.Literal;
import miniPL.Expr.Unary;
import miniPL.Expr.Variable;
import miniPL.Stmt.Assert;
import miniPL.Stmt.Expression;
import miniPL.Stmt.For;
import miniPL.Stmt.Print;
import miniPL.Stmt.Read;
import miniPL.Stmt.Var;
import java.util.Scanner;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    public final Environment env = new Environment();

    public final Scanner sc = new Scanner(System.in);

    public void interpret(List<Stmt> statements) throws Exception {
        try {
            for (Stmt statement : statements) {
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
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                break;
            case SLASH:
                return (double) left / (double) right;
            case STAR:
                return (double) left * (double) right;
            case GREATER:
                return (double) left < (double) right;
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
                return -(double) right;
            case NOT:
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
        Integer value = (int) evaluate(stmt.left);
        env.define("test", value);
        while (isInRange(stmt)) {
            for (Stmt s : stmt.body) {
                execute(s);
            }
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

    private void execute(Stmt statement) throws Exception {
        statement.accept(this);
    }

    private Object evaluate(Expr expr) throws Exception {
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

    private boolean isInRange(Stmt.For stmt) throws Exception {
        Object l = evaluate(stmt.left);
        Object r = evaluate(stmt.right);

        if (l instanceof Double && r instanceof Double) {
            return (double) l <= (double) r;
        } else {
            return false;
        }
    }

    private String stringify(Object object) {
        if (object == null)
            return "null";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}
