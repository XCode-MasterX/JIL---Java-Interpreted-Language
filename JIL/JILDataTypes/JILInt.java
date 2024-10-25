package JILDataTypes;

import java.util.HashMap;

import com.google.common.base.Predicate;

import JILBase.jil;
import JILUtils.JILFunction;
import JILExceptions.*;

public class JILInt extends JILNumber{
    private long value;

    @SuppressWarnings("rawtypes")
    private HashMap<String, JILFunction> functions;

    public JILInt(final long x, final boolean constant) {
        super(constant);
        value = x;
        wasSet = true;

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
                if(args.length > 1) {
                    jil.logger.write("Too many arguments. Only expect one argument.");
                    System.exit(1);
                }
                if(args[0] instanceof Integer val)
                    return lshift(val);
                return null;
            }
        });


        functions.put("binary", new JILFunction<JILString>() {
            public JILString call(Object... args) {
                if(args.length > 0) {
                    jil.logger.write("Too many arguments. No arguments expected.");
                    System.exit(1);
                }
                
                final String x = binaryString();
                return new JILString(x);
            }
        });

        functions.put("asString", new JILFunction<JILString>() {
            public JILString call(Object... args) {
                if(args.length > 0) {
                    jil.logger.write("Too many arguments. No arguments expected.");
                    System.exit(1);
                }
                
                return asString();
            }
        });
    }

    public JILInt(boolean isConstant) {
        super(isConstant);
        wasSet = false;
    }

    public JILInt(long x) {
        super(false);
        value = x;
        wasSet = true;
    }

    public Object getValue(final short line) throws ValueNotSetException { 
        valueIsSet(line);
        return Long.valueOf(value);
    }

    public void setValue(final Object arg, final short line) throws ConstantValueEditException, WrongCastException{ 
        Predicate<Object> condition = (x) ->  x instanceof Long || x instanceof Integer;
        typeIsCompatible(arg, condition, "The value can't be read as an int.", line);
        valueIsConstant(line);

        value = Long.parseLong(arg.toString());
        wasSet = true;
    }

    public String binaryString() { return Long.toBinaryString(value); }

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