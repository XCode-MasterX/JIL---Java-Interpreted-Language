package JILUtils;

import JILDataTypes.JILType;

public class JILCaster<T extends JILType> {
    private final Class<T> passClass;

    public JILCaster(Class<T> type) { passClass = type; }

    public T call(Object arg) {
        return passClass.cast(arg);
    }

    public String toString() {
        return "JILCaster -> " + passClass.getSimpleName();
    }
}