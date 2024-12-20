package JILBase;

public enum TokenType {
    // DATA TYPES
    BOOL, CHAR, DECIMAL, INT, STRING, VOID,

    // Types of constants
    CHAR_CONSTANT, DECIMAL_CONSTANT, INT_CONSTANT, STRING_CONSTANT, EOF,

    COMMENT,

    VARIABLE, FUNCTION,

    //Operators
    LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL, EQUAL, SEMICOLON,
    ASSIGNMENT, ADD, SUB, MUL, DIV, MOD, POWER,
    ADD_EQUAL, SUB_EQUAL, MUL_EQUAL, DIV_EQUAL, MOD_EQUAL, POWER_EQUAL,
    DOT, INCREMENT, DECREMENT, COMMA, COLON,
    NOT_EQUAL, OR, AND, NOT,
    BITWISE_AND, BITWISE_OR, BITWISE_NOT, BITWISE_XOR, RIGHT_SHIFT, LEFT_SHIFT,
    AND_EQUAL, OR_EQUAL, XOR_EQUAL, RSHIFT_EQUAL, LSHIFT_EQUAL,
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, NEWLINE,

    // Keywords
    TRUE, FALSE, IF, ELSE, RETURN, WHILE, FOR, CONST, AS,
    DECLARE, DRIVER, FUNC, IMPORT, INITIAL, NULL, TEMP;
}