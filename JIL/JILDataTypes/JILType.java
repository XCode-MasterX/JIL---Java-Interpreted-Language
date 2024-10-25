package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

import java.util.function.Predicate;

public abstract class JILType {
    final boolean isConstant;
    boolean wasSet;

    JILType(boolean constant) {
        isConstant = constant;
    }
    
    abstract public void setValue(Object arg, short line) throws WrongCastException, ConstantValueEditException;
    abstract public Object getValue(short line)  throws ValueNotSetException;

    public void valueIsConstant(final short line) throws ConstantValueEditException {
        if(isConstant && wasSet)
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.", line);
    }

    public void valueIsSet(final short line) throws ValueNotSetException {
        if(!wasSet) 
            throw new ValueNotSetException("The value was not previously set.", line);
    }

    public void typeIsCompatible(Object arg, Predicate<Object> checker, final String msg, final short line) throws WrongCastException{
        if(!checker.test(arg))
            throw new WrongCastException(msg, line);
    }
}