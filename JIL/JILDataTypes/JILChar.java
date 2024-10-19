package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.WrongCastException;

public class JILChar {
    private char value;
    private boolean isConstant = false;

    public double getValue() { return value; }
    public void setValue(String resetValue) throws ConstantValueEditException, WrongCastException{ 
        if(!isConstant) {
            if(resetValue.length() == 1)
                value = resetValue.charAt(0);
            else
                throw new WrongCastException("You can't store a string as a character.");
        }
        else
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.");
    }

    public String toString() { return String.valueOf(value); }
}
