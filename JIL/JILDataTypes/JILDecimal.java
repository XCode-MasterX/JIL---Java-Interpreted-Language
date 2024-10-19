package JILDataTypes;

import JILExceptions.ConstantValueEditException;
import JILExceptions.WrongCastException;

public class JILDecimal extends JILNumber{
    private double value = 0;

    public JILDecimal(String x, boolean constant) throws WrongCastException{
        super(constant);
        try {
            value = Double.parseDouble(x);
        }
        catch(NumberFormatException e) {
            throw new WrongCastException(String.format("Can't convert %d into float value.\n", x));
        }
    }

    public JILDecimal(double x, boolean constant) throws WrongCastException{
        super(constant);
        try {
            value = x;
        }
        catch(NumberFormatException e) {
            throw new WrongCastException(String.format("Can't convert %d into float value.\n", x));
        }
    }

    public double getValue() { return value; }
    public void setValue(String resetValue) throws ConstantValueEditException, WrongCastException{ 
        if(!super.isConstant) {
            try{
                value = Float.parseFloat(resetValue);
            }
            catch(NumberFormatException e) {
                throw new WrongCastException(String.format("Can't convert %d into float value.\n", resetValue));
            }
        }
        else
            throw new ConstantValueEditException("The value is a constant. You can't edit constant values.");
    }
}