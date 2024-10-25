package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

public class JILChar extends JILType{
    private char value;
    private boolean wasSet = false;

    public JILChar(char value, boolean isConstant) {
        super(isConstant);
        this.value = value;
        wasSet = true;
    }

    public JILChar(boolean isConstant) {
        super(isConstant);
        wasSet = false;
    }

    public Object getValue(final short line) throws ValueNotSetException{
        if(!wasSet)
            throw new ValueNotSetException("The value was not set previously.", line);
        return Character.valueOf(value); 
    }

    public void setValue(final Object arg, final short line) throws ConstantValueEditException, WrongCastException{
        if(!(arg instanceof Character || arg instanceof String))
            throw new WrongCastException("The value can't be read as a character.", line);
        
        if(isConstant && wasSet)
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.", line);
        
        value = (arg instanceof Character) ? (Character)arg : ((String) arg).charAt(0);
        wasSet = true;
    }

    public String toString() { return String.valueOf(value); }
}
