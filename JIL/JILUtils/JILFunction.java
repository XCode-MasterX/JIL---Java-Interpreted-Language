package JILUtils;

public abstract class JILFunction<T> {
    public T call(final int line, Object... args) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'call'");
    }
    
    public T call() {
        throw new UnsupportedOperationException("Unimplemented method 'call'");
    }
}