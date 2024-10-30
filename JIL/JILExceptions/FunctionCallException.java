package JILExceptions;

public class FunctionCallException extends Exception{
    public final int line;
    
    public FunctionCallException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}
