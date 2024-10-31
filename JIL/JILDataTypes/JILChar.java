package JILDataTypes;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import JILBase.TokenType;
import JILBase.jil;
import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

public class JILChar extends JILType{
    private char value;
    private boolean wasSet = false;

    public JILChar(char value, boolean isConstant) {
        super(isConstant, TokenType.CHAR);
        this.value = value;
        wasSet = true;
    }

    public JILChar(boolean isConstant) {
        super(isConstant, TokenType.CHAR);
        wasSet = false;
    }

    public static JILChar createDefault() { return new JILChar('\0', false); }
    public static JILChar createDefaultConstant() { return new JILChar(true); }

    public Object getValue(final int line) throws ValueNotSetException{
        if(!wasSet)
            throw new ValueNotSetException("The value was not set previously.", line);
        return Character.valueOf(value);
    }

    public void setValue(final Object arg, final int line) throws ConstantValueEditException, WrongCastException{
        if(!(arg instanceof Character || arg instanceof String))
            throw new WrongCastException("The value can't be read as a character.", line);
        
        if(isConstant && wasSet)
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.", line);
        
        value = (arg instanceof Character) ? (Character)arg : ((String) arg).charAt(0);
        wasSet = true;
    }

    public String toString() {
        return "JILChar: " + value + "(" + (value + 0) + ")"; 
    }

    private char convert(Object operand, final int line) {
        char op = 0;

        try {
            if(operand instanceof JILChar x) op = (Character) x.getValue(line);
            else
                jil.error(line, "The operand is not compatible with int.");
        }
        catch(ValueNotSetException e) { e.printStackTrace(); jil.logger.write(e.getMessage()); }

        return op;
    }

    @Override
    public JILType add(Object operand, int line) {
        char op = convert(operand, line);
        System.out.println(value + " + " + op + " = " + (value + op));
        this.value = (char) (value + op);
        return this;
    }

    @Override
    public JILType sub(Object operand, int line) {
        char op = convert(operand, line);
        this.value = (char) Math.max(0, value - op);
        return this;
    }

    @Override
    public JILType mul(Object operand, int line) {
        jil.error(line, "The multiplication operation is not supported for decimals.");
        return null;
    }

    @Override
    public JILType div(Object operand, int line) {
        jil.error(line, "The division operation is not supported for decimals.");
        return null;
    }

    @Override
    public JILType mod(Object operand, int line) {
        jil.error(line, "The mod operation is not supported for decimals.");
        return null;
    }

    @Override
    public JILType power(Object operand, int line) {
        jil.error(line, "The power operation is not supported for Strings.");
        return null;
    }

    @Override
    public JILType and(Object operand, int line) {
        char op = convert(operand, line);
        this.value = (char) (value & op);
        return this;
    }

    @Override
    public JILType or(Object operand, int line) {
        char op = convert(operand, line);
        this.value = (char) (value | op);
        return this;
    }

    @Override
    public JILType xor(Object operand, int line) {
        char op = convert(operand, line);
        this.value = (char) (value ^ op);
        return this;
    }

    @Override
    public JILType not(int line) {
        this.value = (char) (~value);
        return this;
    }
}
