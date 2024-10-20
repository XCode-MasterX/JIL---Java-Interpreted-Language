package JILDataTypes;

import java.util.HashMap;

import JILBase.jil;
import JILUtils.JILFunction;
import JILExceptions.*;

public class JILInt extends JILNumber{
    private long value;

    @SuppressWarnings("rawtypes")
    private HashMap<String, JILFunction> functions;

    public JILInt(String x, boolean constant) throws WrongCastException {
        super(constant);
        try {
            value = Long.parseLong(x);
        }
        catch(NumberFormatException e) {
            throw new WrongCastException(String.format("Can't convert %d into float value.\n", x));
        }

        functions = new HashMap<>();

        functions.put("rshift", new JILFunction<JILInt>() {
            public JILInt call(Object... args) {
                if(args.length > 1) {
                    jil.logger.write("Too many arguments. Only expect one argument.");
                    System.exit(1);
                }
                if(args[0] instanceof Integer val)
                    return rshift(val);
                return null;
            }
        });

        functions.put("lshift", new JILFunction<JILInt>() {
            public JILInt call(Object... args) {
                if(args[0] instanceof Integer val)
                    return lshift(val);
                return null;
            }
        });
    }

    public JILInt(String x) throws WrongCastException {
        super(false);
        try {
            value = Long.parseLong(x);
        }
        catch(NumberFormatException e) {
            throw new WrongCastException(String.format("Can't convert %d into float value.\n", x));
        }
    }

    public JILInt(long val, boolean constant) {
        super(constant);
        value = val;
    }

    public JILInt(long val) {
        super(false);
        value = val;
    }

    public long getValue() { return value; }
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

    public String binstring() { return Long.toBinaryString(value); }

    public JILInt lshift(int a) {
        long ret = value & (int)(Math.pow(1, a) - 1) << a;
        value <<= a;
        return new JILInt(ret);
    }

    public JILInt rshift(int a) {
        long ret = value & (int)(Math.pow(2, a) - 1);
        value >>= a;
        return new JILInt(ret);
    }

    public JILString asString() { return new JILString(this.toString()); }

    public String toString() { return String.valueOf(value); }

    public void call(String funcName, Object... args) {
        functions.get(funcName).call(args);
    }
}