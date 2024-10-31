package JILExceptions;

public class InvalidOperationException extends Exception{
    public final int line;
    
    public InvalidOperationException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}
