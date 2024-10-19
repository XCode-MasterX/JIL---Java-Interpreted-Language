package JILDataTypes;

import JILExceptions.ConstantValueEditException;

public class JILString {
    private StringBuilder value;
    private boolean isConstant = false;

    public JILString(String x) {
        value = new StringBuilder(x);
    }

    public String getValue() { return value.toString(); }
    public void setValue(String resetValue) throws ConstantValueEditException {
        if(!isConstant) {
            value.delete(0, value.length());
            value.append(resetValue);
        }
        else
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.");
    }

    public String toString() { return value.toString(); }
}