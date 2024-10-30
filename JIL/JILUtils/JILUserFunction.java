package JILUtils;

public abstract class JILUserFunction<T> {
    final short startLine, endLine;

    public JILUserFunction(final short startLine, final short endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    abstract public T call(Object... args);
}