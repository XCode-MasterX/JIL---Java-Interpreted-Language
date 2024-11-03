package JILUtils;

public class JILConstructor<T> {
    private final JILFunction<T> defaultConstructor;
    private final JILFunction<T> defaultConstant;

    public JILConstructor(JILFunction<T> defCon, JILFunction<T> defConst) {
        defaultConstant = defConst;
        defaultConstructor = defCon;
    }

    public T constructDefault() {
        return defaultConstructor.call();
    }

    public T constructConstant() {
        return defaultConstant.call();
    }
}
