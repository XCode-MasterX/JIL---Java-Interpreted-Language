package JILDataTypes;

import JILBase.TokenType;
import JILBase.jil;
import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

public class JILVoid extends JILType {

    public JILVoid() {  super(true, TokenType.VOID); }

    @Override
    public void setValue(Object arg, int line) throws WrongCastException, ConstantValueEditException {
        jil.error(line, "The operation is not supported for Void.");
    }

    @Override
    public Object getValue(int line) throws ValueNotSetException {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType add(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType sub(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType mul(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType div(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType mod(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType power(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType and(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType or(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType xor(Object operand, int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType not(int line) {
        jil.error(line, "The operation is not supported for Void.");
        return null;
    }

    @Override
    public JILType getCopy() {
        return this;
    }
}
// this class will contain nothing because it's void. Don't change this.