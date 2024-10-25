package JILExceptions;

public class WrongCastException extends Exception {
    private final short line;
    
    public WrongCastException(String msg, short line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}
