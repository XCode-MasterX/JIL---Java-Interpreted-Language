package JILBase;
enum TokenType {
    BOOL_CONSTANT, CHAR_CONSTANT, DECIMAL_CONSTANT, INT_CONSTANT, STRING_CONSTANT,

    COMMENT,

    VARIABLE, DATA_TYPE, FUNCTION, OPERATOR,

    BOOL, CALL, CHAR, DECIMAL, DECLARE, IMPORT, INITIAL, INT, STRING,TEMP;
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