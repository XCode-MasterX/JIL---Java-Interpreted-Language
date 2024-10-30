package JILDataTypes;

import java.util.HashMap;

import com.google.common.base.Predicate;

import JILBase.jil;
import JILUtils.JILFunction;
import JILExceptions.*;

public class JILInt extends JILNumber{
    private long value;

    public JILInt(final long x, final boolean constant) {
        super(constant);
        value = x;
        wasSet = true;

        initFunctions();
    }

    public JILInt(boolean isConstant) {
        super(isConstant);
        wasSet = false;
        initFunctions();
    }

    public void initFunctions() {
        functions = new HashMap<>();

        functions.put("rshift", new JILFunction<JILInt>() {
            public JILInt call(final int line, Object... args) {
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
            public JILInt call(final int line, Object... args) throws FunctionCallException, WrongCastException {
                if(args.length > 1)
                    throw new FunctionCallException("Too many arguments. Only expect one argument.", line);

                if(args[0] instanceof Long val)
                    return lshift(val);
                
                throw new WrongCastException("The passed value can't be read as int. You need to pass an int to call this function.", line);
            }
        });


        functions.put("binary", new JILFunction<JILString>() {
            public JILString call(final int line, Object... args) throws Exception{
                if(args.length > 0) 
                    throw new FunctionCallException("Too many arguments for the function that doesn't accept any.", line);
                
                final String x = binaryString();
                return new JILString(x, false);
            }
        });

        functions.put("asString", new JILFunction<JILString>() {
            public JILString call(final int line, Object... args) throws FunctionCallException{
                if(args.length > 0)
                    throw new FunctionCallException("Too many arguments. No arguments expected.", line);
                
                return asString();
            }
        });
    }

    public Object getValue(final int line) throws ValueNotSetException { 
        valueIsSet(line);
        return Long.valueOf(value);
    }

    public void setValue(final Object arg, final int line) throws ConstantValueEditException, WrongCastException{
        Predicate<Object> condition = (x) ->  x instanceof Long || x instanceof Integer;
        typeIsCompatible(arg, condition, "The value " + arg +" can't be read as an int.", line);
        valueIsConstant(line);

        value = (Long) arg;
        super.wasSet = true;
    }

    public String binaryString() { return Long.toBinaryString(value); }

    public JILInt lshift(long a) {
        long ret = value & (int)(Math.pow(1, a) - 1) << a;
        value <<= a;
        return new JILInt(ret, false);
    }

    public JILInt rshift(long a) {
        long ret = value & (int)(Math.pow(2, a) - 1);
        value >>= a;
        return new JILInt(ret, false);
    }

    public JILString asString() { return new JILString(this.toString(), false); }

    public String toString() { return String.valueOf(value); }

    public void call(final String funcName, final short line, Object... args) {
        try {
            functions.get(funcName).call(line, args);
        }
        catch(FunctionCallException e) {
            jil.logger.write(e.toString());
            System.exit(1);
        }
        catch(Exception e) {
            jil.logger.write(e.toString());
            System.exit(1);
        }
    }

    public static JILInt createDefault() { return new JILInt(0, false); }
    public static JILInt createDefaultConstant() { return new JILInt(true); }
}