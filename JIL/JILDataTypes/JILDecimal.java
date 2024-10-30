package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

import java.util.function.Predicate;

public class JILDecimal extends JILNumber{
    private double value = 0;

    public JILDecimal(double x, boolean constant){
        super(constant);
        value = x;
        wasSet = true;
    }

    public JILDecimal(boolean constant) {
        super(constant);
        wasSet = false;
    }

    public JILDecimal createDefault() { return new JILDecimal(0, false); }
    public JILDecimal createDefaultConstant() { return new JILDecimal(true); }

    public Object getValue(final int line) throws ValueNotSetException{
        valueIsSet(line);
        return Double.valueOf(value);
    }

    public void setValue(final Object arg, final int line) throws ConstantValueEditException, WrongCastException{
        Predicate<Object> condition = (x) ->  x instanceof Double || x instanceof Float || x instanceof Long || x instanceof Integer;
        typeIsCompatible(arg, condition, "The value can't be read as a decimal", line);
        valueIsConstant(line);

        value = Double.parseDouble(arg.toString());
    }
}