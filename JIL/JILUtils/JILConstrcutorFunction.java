package JILUtils;

public abstract class JILConstrcutorFunction<T> {
    public T call(final int line, Object... args) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'call'");
    }
    
    public T call() {
        throw new UnsupportedOperationException("Unimplemented method 'call'");
    }
}