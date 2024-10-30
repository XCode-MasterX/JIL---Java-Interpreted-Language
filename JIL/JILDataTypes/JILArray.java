package JILDataTypes;

import JILBase.jil;
import JILExceptions.ValueNotSetException;

public class JILArray<T> {
    private Object arr[];

    public JILArray(int size) {
        arr = new Object[size];
    }

    public JILArray(JILInt size, final short line) {
        try {
            int sz = (int)size.getValue(line);
            arr = new Object[sz];
        }
        catch(ValueNotSetException e) {
            jil.logger.write(e.toString());
        }
    }

    public Object get(int index) { return arr[index]; }

    public JILString asString(final short line) {
        JILString str = new JILString("[", false);
        for(Object x : arr) str.append(x.toString(), line);
        str.append("]", line);

        return str;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Object x : arr) sb.append(x.toString());
        return sb.toString();
    }

    public Object[] getArray() {
        return arr;
    }
}