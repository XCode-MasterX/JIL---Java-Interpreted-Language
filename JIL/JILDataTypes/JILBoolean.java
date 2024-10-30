package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

import java.util.function.Predicate;

public class JILBoolean extends JILType {
    private boolean value;

    public JILBoolean(boolean isConstant) {
        super(isConstant);
        wasSet = false;
    }

    public JILBoolean(boolean value, boolean isConstant) {
        super(isConstant);
        this.value = value;
        wasSet = true;
    }

    public static JILBoolean createDefault() { return new JILBoolean(false, false); }
    public static JILBoolean createDefaultConstant() { return new JILBoolean(true); }

    public void setValue(final Object arg, final int line) throws WrongCastException, ConstantValueEditException {
        Predicate<Object> condition = (x) -> x instanceof Boolean ;
        typeIsCompatible(arg, condition, "The value can't be read as a boolean.", line);
        valueIsConstant(line);

        value = ((Boolean) arg).booleanValue();
        wasSet = true;
    }

    public Object getValue(final int line) throws ValueNotSetException {
        valueIsSet(line);
        return Boolean.valueOf(value);
    }

    public String toString() { return String.valueOf(value); }
}