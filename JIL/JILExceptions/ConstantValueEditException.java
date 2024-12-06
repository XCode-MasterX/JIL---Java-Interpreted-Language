package JILExceptions;

public class ConstantValueEditException extends Exception{
    public final int line;
    
    public ConstantValueEditException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}