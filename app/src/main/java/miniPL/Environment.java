package miniPL;

import java.util.HashMap;

public class Environment {

    private final HashMap<String, Object> variables = new HashMap<>();

    public void define(String identifier, Object value) {
        this.variables.put(identifier, value);
    }

    public Object get(Token identifier) throws Exception {
        if (variables.containsKey(identifier.lexeme)) {
            return variables.get(identifier.lexeme);
        }
        throw new Exception("Unknown variable");
    }

    public void assign(Token name, Object value) throws Exception {
        if (variables.containsKey(name.lexeme)) {
            variables.put(name.lexeme, value);
            return;
        }
        throw new Exception("Undefined variable '" + name.lexeme + "'.");
    }
}
