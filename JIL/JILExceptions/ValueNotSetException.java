package JILExceptions;

public class ValueNotSetException extends Exception{
    public final int line;
    
    public ValueNotSetException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}