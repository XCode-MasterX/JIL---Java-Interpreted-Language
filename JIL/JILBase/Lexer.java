package JILBase;
enum TokenType {
    BOOL_CONSTANT,
    CHAR_CONSTANT,
    DECIMAL_CONSTANT,
    INT_CONSTANT,
    STRING_CONSTANT,
    VARIABLE,
    DATA_TYPE,
    FUNCTION,
    IMPORT
}

class Token {
    String token;
    TokenType type;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }
}

public class Lexer {

    public Lexer(String program) {

    }
}