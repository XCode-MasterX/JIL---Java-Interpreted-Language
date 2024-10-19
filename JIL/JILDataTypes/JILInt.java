package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.WrongCastException;

public class JILInt {
    private long value;
    private boolean isConstant = false;
    
    public double getValue() { return value; }
    public void setValue(String resetValue) throws ConstantValueEditException, WrongCastException{ 
        if(!isConstant) {
            try{
                value = Long.parseLong(resetValue);
            }
            catch(NumberFormatException e) {
                throw new WrongCastException(String.format("Can't convert %d into float value.\n", resetValue));
            }
        }
        else
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.");
    }

    public String toString() { return String.valueOf(value); }
}