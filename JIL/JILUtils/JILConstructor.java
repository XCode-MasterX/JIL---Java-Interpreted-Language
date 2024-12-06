package JILUtils;

public class JILConstructor<T> {
    private final JILConstrcutorFunction<T> defaultConstructor;
    private final JILConstrcutorFunction<T> defaultConstant;

    public JILConstructor(JILConstrcutorFunction<T> defCon, JILConstrcutorFunction<T> defConst) {
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
