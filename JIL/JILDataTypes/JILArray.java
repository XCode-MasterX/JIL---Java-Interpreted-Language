package JILDataTypes;

public class JILArray<T> {
    private Object arr[];

    public JILArray(int size) {
        arr = new Object[size];
    }

    public JILArray(JILInt size) {
        this((int)size.getValue());
    }

    public Object get(int index) { return arr[index]; }

    public JILString asString() {
        JILString str = new JILString("[");
        for(Object x : arr) str.append(x.toString());
        str.append("]");

        return str;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Object x : arr) sb.append(x.toString());
        return sb.toString();
    }
}