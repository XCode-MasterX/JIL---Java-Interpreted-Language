package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILBase.TokenType;
import JILBase.jil;

import java.util.function.Predicate;

public class JILBoolean extends JILType {
    private boolean value;

    public JILBoolean(boolean isConstant) {
        super(isConstant, TokenType.BOOL);
        wasSet = false;
    }

    public JILBoolean(boolean value, boolean isConstant) {
        super(isConstant, TokenType.BOOL);
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

    public String toString() { return value + ""; }

    private boolean convert(Object operand, final int line) throws ValueNotSetException {
        boolean op = false;

        if(operand instanceof JILBoolean x) op = (Boolean) x.getValue(line);
        if(operand instanceof JILInt x) {
            long val = (Long) x.getValue(line);
            if(val > 0) op = true;
            else if(val == 0) op = false;
            else jil.error(line, "To convert an int to boolean it needs to greater than 0 for 'true' or equal to 0 for 'false'.");
        }
        else
            jil.error(line, "The operand is not compatible with int.");

        return op;
    }

    public JILType getCopy() {
        return new JILBoolean(value, false);
    }

    @Override
    public JILType add(Object operand, int line) {
        boolean op = false;

        try { op = convert(operand, line); }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        this.value |= op;
        return this;
    }

    @Override
    public JILType sub(Object operand, int line) {
        boolean op = false;

        try { op = convert(operand, line); }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        this.value &= !op;
        return this;
    }

    @Override
    public JILType mul(Object operand, int line) {
        boolean op = false;

        try { op = convert(operand, line); }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        this.value &= op;
        return new JILInt(value ? 1 : 0, false);
    }

    @Override
    public JILType div(Object operand, int line) {
        jil.error(line, "The division operation is not supported for boolean.");
        return null;
    }

    @Override
    public JILType mod(Object operand, int line) {
        jil.error(line, "The division operation is not supported for boolean.");
        return null;
    }

    @Override
    public JILType power(Object operand, int line) {
        jil.error(line, "The power operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType and(Object operand, int line) {
        boolean op = false;

        try { op = convert(operand, line); }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        this.value &= op;
        return this;
    }

    @Override
    public JILType or(Object operand, int line) {
        boolean op = false;

        try { op = convert(operand, line); }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        this.value |= op;
        return this;
    }

    @Override
    public JILType xor(Object operand, int line) {
        boolean op = false;

        try { op = convert(operand, line); }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        this.value ^= op;
        return this;
    }

    @Override
    public JILType not(int line) {
        this.value = !this.value;
        return this;
    }
}