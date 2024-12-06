package JILBase;

import JILDataTypes.JILType;

public interface StoreVariables {
    public Object getVariableValue(final Token search);
    public JILType getVariable(final String search);
}
