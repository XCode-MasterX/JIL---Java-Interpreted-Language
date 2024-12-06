package JILExceptions;

public class WrongCastException extends Exception {
    public final int line;
    
    public WrongCastException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}
