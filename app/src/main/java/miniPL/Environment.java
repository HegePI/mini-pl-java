package miniPL;

import java.util.HashMap;

public class Environment {

    private final HashMap<String, Object> variables = new HashMap<>();

    public void define(String identifier, Object value) {
        this.variables.put(identifier, value);
    }

    public Object get(Token identifier) {
        if (variables.containsKey(identifier.lexeme)) {
            return variables.get(identifier.lexeme);
        }
        printEnvironmentError("trying to fetch undefined variable");
        return null;
    }

    public boolean keyExists(Token name) {
        return this.variables.containsKey(name.lexeme);

    }

    private void printEnvironmentError(String msg) {
        System.out.println(String.format("Environment/Variable error: " + msg));
        System.exit(1);
    }
}
