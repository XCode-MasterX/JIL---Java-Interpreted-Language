package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILBase.TokenType;
import JILBase.jil;

import java.util.function.Predicate;


public class JILString extends JILType{
    private StringBuilder value;

    public JILString(String x, boolean constant) {
        super(constant, TokenType.STRING);
        value = new StringBuilder(x);
        wasSet = true;
    }

    public JILString(boolean constant) {
        super(constant, TokenType.STRING);
        wasSet = false;
    }

    public void append(JILString add, final int line)   { this.value.append(add.value); }
    public void append(String add, final int line)      { this.value.append(add);       }

    public char charAt(JILInt index, final int line)    {
        char returnValue = '\0';
        try{
            returnValue = charAt((int)index.getValue(line), line);  
        }
        catch(ValueNotSetException e) {
            jil.logger.write(e.toString());
        }

        return returnValue;
    }
    public char charAt(int index, final int line)       { return this.value.charAt(index); }

    public Object getValue(final int line) throws ValueNotSetException {
        valueIsSet(line);
        return value.toString();
    }

    public void setValue(final Object arg, final int line) throws WrongCastException, ConstantValueEditException {
        Predicate<Object> condition = (x) ->  x instanceof String || x instanceof Character;
        typeIsCompatible(arg, condition, "The value can't be read as a decimal", line);
        valueIsConstant(line);

        value = new StringBuilder(arg.toString());
        wasSet = true;
    }

    public String toString() { return value.toString(); }

    private String convert(Object operand, final int line) {
        String op = "";

        try {
            if(operand instanceof JILString x) op = (String) x.getValue(line);
            else if(operand instanceof JILChar x) op = x.getValue(line) + "";
            else if(operand instanceof JILInt x) op = x.getValue(line) + "";
            else if(operand instanceof JILDecimal x) op = x.getValue(line) + "";
            else if(operand instanceof JILBoolean x) op = x.getValue(line) + "";
        }
        catch(ValueNotSetException e) {

        }
        return op;
    }

    @Override
    public JILType add(Object operand, int line) {
        String op = convert(operand, line);
        this.value.append(op);
        return this;
    }

    @Override
    public JILType sub(Object operand, int line) {
        jil.error(line, "The subtraction operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType mul(Object operand, int line) {
        long op = 0;
        
        try {
            if(operand instanceof JILInt x) op = (Long) x.getValue(line);
            else
                throw new WrongCastException("This value can't be used for string repetition.", line);
        }
        catch(Exception e) {
            e.printStackTrace();
            jil.error(line, e.getMessage());
        }
        
        final String startString = value.toString();

        value.delete(0, value.length());
        for(;op > 0; op--) value.append(startString);

        return this;
    }

    public JILType getCopy() {
        return new JILString(value.toString(), false);
    }

    @Override
    public JILType div(Object operand, int line) {
        jil.error(line, "The division operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType mod(Object operand, int line) {
        jil.error(line, "The mod operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType power(Object operand, int line) {
        jil.error(line, "The power operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType and(Object operand, int line) {
        jil.error(line, "The and operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType or(Object operand, int line) {
        jil.error(line, "The or operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType xor(Object operand, int line) {
        jil.error(line, "The xor operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType not(int line) {
        jil.error(line, "The not operation is not supported for Strings.");
        return null;
    }
}