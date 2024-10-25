package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

public class JILBoolean extends JILType {
    private boolean value;
    private boolean wasSet = false;

    JILBoolean(boolean isConstant) {
        super(isConstant);
        wasSet = false;
    }

    JILBoolean(boolean value, boolean isConstant) {
        super(isConstant);
        this.value = value;
        wasSet = true;
    }

    @Override
    public void setValue(final Object arg, final short line) throws WrongCastException, ConstantValueEditException {
        if(!(arg instanceof Boolean))
            throw new WrongCastException("The value can't be read as a bool.", line);

        if(isConstant && wasSet)
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.", line);

        value = ((Boolean) arg).booleanValue();
        wasSet = true;
    }

    @Override
    public Object getValue(final short line) throws ValueNotSetException {
        if(!wasSet)
            throw new ValueNotSetException("The value was not previously set.", line);
        
        return Boolean.valueOf(value);
    }

    public String toString() { return String.valueOf(value); }
}