package JILBase;

import JILDataTypes.JILType;

public class Variable {
    private JILType varValue;
    final String name;

    public Variable(String name, JILType value) {
        this.name = name;
        this.varValue = value;
    }

    public JILType getValue() {
        return varValue;
    }

    public String toString() { return name + ": " + varValue; }
}
