package JILDataTypes;

import JILBase.jil;
import JILBase.TokenType;
import JILExceptions.ConstantValueEditException;
import JILExceptions.FunctionCallException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILUtils.JILFunction;

import java.util.function.Predicate;

import java.util.HashMap;

public abstract class JILType {
    public final TokenType dataType;
    final boolean isConstant;
    public boolean wasSet;
    HashMap<String, JILFunction> functions;

    JILType(boolean constant, TokenType thisType) {
        isConstant = constant;
        dataType = thisType;
    }
    
    abstract public void setValue(Object arg, final int line) throws WrongCastException, ConstantValueEditException;
    abstract public Object getValue(final int line)  throws ValueNotSetException;
    abstract public JILType add(Object operand, final int line);
    abstract public JILType sub(Object operand, final int line);
    abstract public JILType mul(Object operand, final int line);
    abstract public JILType div(Object operand, final int line);
    abstract public JILType mod(Object operand, final int line);
    abstract public JILType power(Object operand, final int line);
    abstract public JILType and(Object operand, final int line);
    abstract public JILType or(Object operand, final int line);
    abstract public JILType xor(Object operand, final int line);
    abstract public JILType not(final int line);
    abstract public JILType getCopy();

    public void valueIsConstant(final int line) throws ConstantValueEditException {
        if(isConstant && wasSet)
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.", line);
    }

    public void valueIsSet(final int line) throws ValueNotSetException {
        if(!wasSet)
            throw new ValueNotSetException("The value was not previously set.", line);
    }

    public void typeIsCompatible(Object arg, Predicate<Object> checker, final String msg, final int line) throws WrongCastException{
        if(!checker.test(arg))
            throw new WrongCastException(msg, line);
    }

    public void call(final String funcName, final int line, Object... args) {
        try {
            functions.get(funcName).call(line, args);
        }
        catch(Exception e) {
            jil.logger.write(e.toString());
            System.exit(1);
        }
    }
}