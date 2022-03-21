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

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (Exception error) {
            printInterpretError(error.getMessage());
        }
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (left.getClass() != right.getClass()) {
            printInterpretError("left and right expressions in binary expression are of different types%nleft: "
                    + left.getClass().getSimpleName() + "%nright: " + right.getClass().getSimpleName());
        }

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
            case AND:
                try {
                    return isTruthy((boolean) left) && isTruthy((boolean) right);
                } catch (Exception e) {
                    printInterpretError("'&' can be only applied between boolean values");
                }
        }

        return null;
    }

    @Override
    public Object visitGroupingExpression(GroupingExpression expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpression(LiteralExpression expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression expr) {
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
    public Object visitVariableExpression(VariableExpression expr) {
        return env.get(expr.name);

    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement stmt) {
        Object value = evaluate(stmt.expression);
        System.out.print(String.format(stringify(value)));
        return null;
    }

    @Override
    public Void visitVarStatement(VarStatement stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        env.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitForStatement(ForStatement stmt) {

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
    public Void visitReadStatement(ReadStatement stmt) {

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
    public Void visitAssertStatement(AssertStatement stmt) {
        if (isTruthy(evaluate(stmt.expr))) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
        return null;
    }

    @Override
    public Void visitAssignStatement(AssignStatement stmt) {
        env.define(stmt.name.lexeme, evaluate(stmt.value));
        return null;
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }

    private Object evaluate(Expression expr) {
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

    private boolean isInRange(Statement.ForStatement stmt) {
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

    private void printInterpretError(String msg) {
        System.out.println(String.format("Interpreting error: " + msg));
        System.exit(1);

    }
}
