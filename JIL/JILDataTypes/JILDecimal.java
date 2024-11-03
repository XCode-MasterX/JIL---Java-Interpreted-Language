package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILBase.TokenType;
import JILBase.jil;

import java.util.function.Predicate;

public class JILDecimal extends JILType{
    private double value = 0;

    public JILDecimal(double x, boolean constant){
        super(constant, TokenType.DECIMAL);
        value = x;
        wasSet = true;
    }

    public JILDecimal(boolean constant) {
        super(constant, TokenType.DECIMAL);
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

    public String toString() { return "" + value; }

    private double convert(Object operand, final int line) {
        double op = 0;

        try {
            if(operand instanceof JILChar x) op = (Character) x.getValue(line);
            else if(operand instanceof JILBoolean x) op = (Boolean) x.getValue(line) ? 1 : 0;
            else if(operand instanceof JILInt x) op = (Long) x.getValue(line);
            else if(operand instanceof JILDecimal x) op = (Double) x.getValue(line);
            else {
                jil.error(line, "The operand is not compatible with int.");
            }
        }
        catch(ValueNotSetException e) {
            e.printStackTrace();
            jil.logger.write(e.getMessage());
        }

        return op;
    }

    public JILType getCopy() {
        return new JILDecimal(value, false);
    }

    @Override
    public JILType add(Object operand, int line) {
        double op = convert(operand, line);
        this.value += op;
        return this;
    }

    @Override
    public JILType sub(Object operand, int line) {
        double op = convert(operand, line);
        this.value -= op;
        return this;
    }

    @Override
    public JILType mul(Object operand, int line) {
        double op = convert(operand, line);
        this.value *= op;
        return this;
    }

    @Override
    public JILType div(Object operand, int line) {
        double op = convert(operand, line);
        this.value /= op;
        return this;
    }

    @Override
    public JILType mod(Object operand, int line) {
        double op = convert(operand, line);
        this.value %= op;
        return this;
    }

    @Override
    public JILType power(Object operand, int line) {
        double op = convert(operand, line);
        this.value = Math.pow(value, op);
        return this;
    }

    @Override
    public JILType and(Object operand, int line) {
        jil.error(line, "The and operation is not supported for decimals.");
        return null;
    }

    @Override
    public JILType or(Object operand, int line) {
        jil.error(line, "The or operation is not supported for decimals.");
        return null;
    }

    @Override
    public JILType xor(Object operand, int line) {
        jil.error(line, "The xor operation is not supported for decimals.");
        return null;
    }

    @Override
    public JILType not(int line) {
        jil.error(line, "The not operation is not supported for decimals.");
        return null;
    }
}