package JILBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum TokenType {
    // Types of constants
    CHAR_CONSTANT, DECIMAL_CONSTANT, INT_CONSTANT, STRING_CONSTANT, EOF,

    COMMENT,

    VARIABLE, DATA_TYPE, FUNCTION,

    //Operators
    LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL, EQUAL, SEMICOLON,
    ASSIGNMENT, ADD, SUB, MUL, DIV, DOT, MOD, INCREMENT, DECREMENT, COMMA, COLON,
    NOT_EQUAL, OR, AND, NOT,
    BITWISE_AND, BITWISE_OR, BITWISE_NOT, BITWISE_XOR, RIGHT_SHIFT, LEFT_SHIFT,
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,

    // Keywords
    TRUE, FALSE, IF, ELSE, RETURN, WHILE, FOR,
    BOOL, CHAR, DECIMAL, INT, STRING, VOID,
    CALL, DECLARE, FUNC, IMPORT, INITIAL, NULL, TEMP;
}

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line; 
  
    Token(TokenType type, String lexeme, Object literal, int line) {
      this.type = type;
      this.lexeme = lexeme;
      this.literal = literal;
      this.line = line;
    }
  
    public String toString() {
      return "Line: " + line + " -> " + type + " " + lexeme + " " + literal;
    }
  }

public class Lexer {
    private final String program;
    private final ArrayList<Token> tokens;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("or",      TokenType.OR);
        keywords.put("and",     TokenType.AND);
        keywords.put("if",      TokenType.IF);
        keywords.put("else",    TokenType.ELSE);
        keywords.put("true",    TokenType.TRUE);
        keywords.put("false",   TokenType.FALSE);
        keywords.put("while",   TokenType.WHILE);
        keywords.put("for",     TokenType.FOR);
        keywords.put("import",  TokenType.IMPORT);
        keywords.put("initial", TokenType.INITIAL);
        keywords.put("declare", TokenType.DECLARE);
        keywords.put("call",    TokenType.CALL);
        keywords.put("func",    TokenType.FUNC);
        keywords.put("temp",    TokenType.TEMP);
        keywords.put("nil",     TokenType.NULL);
        keywords.put("return",  TokenType.RETURN);

        keywords.put("void",    TokenType.VOID);
        keywords.put("bool",    TokenType.BOOL);
        keywords.put("char",    TokenType.CHAR);
        keywords.put("decimal", TokenType.DECIMAL);
        keywords.put("int",     TokenType.INT);
        keywords.put("String",  TokenType.STRING);
    }


    public Lexer(String program) {
        this.program = program;
        this.tokens = new ArrayList<>();
    }

    ArrayList<Token> scanTokens() {
        while (!isEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                break;
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '*': addToken(TokenType.MUL); break;
            case ':': addToken(TokenType.COLON); break;
            case '^': addToken(TokenType.BITWISE_XOR); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '"': string(); break;
            case '\'': character(); break;
            case '-': 
                addToken(match('-') ? TokenType.SUB : TokenType.DECREMENT); 
                break;
            case '+': 
                addToken(match('+') ? TokenType.ADD : TokenType.INCREMENT); 
                break;
            case '|':
                addToken(match('|') ? TokenType.OR : TokenType.BITWISE_OR);
                break;
            case '!':
              addToken(match('=') ? TokenType.NOT_EQUAL : TokenType.NOT);
              break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL : TokenType.ASSIGNMENT);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS_THAN);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);
                break;
            case '/':
                if (match('/'))
                  while (peek() != '\n' && !isEnd()) advance();
                else
                  addToken(TokenType.DIV);
                break;      
            case '\n':
                line++;
                break;
            
            default:
                if (isDigit(c)) number();
                else if (isAlpha(c)) identifier();
                else jil.error(line, "Unexpected character.");
            
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = program.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.VARIABLE;
        addToken(type);
    }

    private void character() {
        final int START_LINE = line;
        while(peek() != '\'' && !isEnd()) {
            if(peek() =='\n') line++;
            advance();
        }

        if(isEnd()) {
            jil.error(line, "You have an unterminated character constant, starting from line: " + START_LINE);
            return;
        }

        advance();
        final String value = program.substring(start + 1, current - 1);
        if(value.length() > 1)
            jil.error(line, "A character can't have more than 1 characters within it.");

        addToken(TokenType.CHAR_CONSTANT, value);
    }

    private void string() {
        final int START_LINE = line;

        while(peek() != '"' && !isEnd()) {
            if(peek() =='\n') line++;
            advance();
        }

        if(isEnd()) {
            jil.error(line, "You have an unterminated string, starting from line: " + START_LINE);
            return;
        }

        advance();
        final String value = program.substring(start + 1, current - 1);
        addToken(TokenType.STRING_CONSTANT, value);
    }

    private void number() {
        while (isDigit(peek())) advance();
        
            if (peek() == '.' && isDigit(peekNext())) {
                advance();
        
            while (isDigit(peek())) advance();
        }

        final double d = Double.parseDouble(program.substring(start, current));

        if((int)d - d == 0)
            addToken(TokenType.INT_CONSTANT, (int)d + "");
        else
            addToken(TokenType.DECIMAL_CONSTANT, d + "");
    }

    private char peek() {
        if (isEnd()) return '\0';
        return program.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= program.length()) return '\0';
        return program.charAt(current + 1);
    }

    private boolean match(char expected) {
        if (isEnd()) return false;
        if (program.charAt(current) != expected) return false;
    
        current++;
        return true;
      }

    private void addToken(TokenType type) { addToken(type, null); }

    private void addToken(TokenType type, Object literal) { 
        String text = program.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAlpha(char c) { 
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_' || c == '$');
    }
    private boolean isAlphaNumeric(char c) { return isAlpha(c) || isDigit(c); }
    private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    private boolean isEnd() { return program.length() <= current; }
    private char advance() { return program.charAt(current++); }
}