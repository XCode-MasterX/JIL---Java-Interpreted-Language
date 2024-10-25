package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILBase.jil;

import java.util.function.Predicate;


public class JILString extends JILType{
    private StringBuilder value;

    public JILString(String x) {
        super(false);
        value = new StringBuilder(x);
    }

    public JILString(String x, boolean constant) {
        super(constant);
        value = new StringBuilder(x);
    }

    public void append(JILString add, final short line)   { this.value.append(add.value); }
    public void append(String add, final short line)      { this.value.append(add);       }

    public char charAt(JILInt index, final short line)    {
        char returnValue = '\0';
        try{
            returnValue = charAt((int)index.getValue(line), line);  
        }
        catch(ValueNotSetException e) {
            jil.logger.write(e.toString());
        }

        return returnValue;
    }
    public char charAt(int index, final short line)       { return this.value.charAt(index);                  }

    public Object getValue(final short line) throws ValueNotSetException {
        valueIsSet(line);
        return value.toString();
    }

    public void setValue(final Object arg, final short line) throws WrongCastException, ConstantValueEditException {
        Predicate<Object> condition = (x) ->  x instanceof String || x instanceof Character;
        typeIsCompatible(arg, condition, "The value can't be read as a decimal", line);
        valueIsConstant(line);

        value = new StringBuilder(arg.toString());
        wasSet = true;
    }

    public String toString() { return value.toString(); }
}