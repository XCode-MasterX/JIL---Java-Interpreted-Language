package JILExceptions;

public class VariabelNotDeclaredException extends Exception {
    public final int line;
    
    public VariabelNotDeclaredException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public String toString() {
        return String.format("Error on [line %d] - %s", line, getMessage());
    }
}
