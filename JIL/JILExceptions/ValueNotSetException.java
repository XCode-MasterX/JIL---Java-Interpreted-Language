package JILExceptions;

public class ValueNotSetException extends Exception{
    private final short line;
    
    public ValueNotSetException(String msg, short line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}