package JILBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    final String program;
    private final ArrayList<Token> tokens;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("as",      TokenType.AS);
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
        keywords.put("driver",  TokenType.DRIVER);
        keywords.put("func",    TokenType.FUNC);
        keywords.put("temp",    TokenType.TEMP);
        keywords.put("null",    TokenType.NULL);
        keywords.put("as",      TokenType.AS);
        keywords.put("return",  TokenType.RETURN);
        
        keywords.put("const",   TokenType.CONST);
        keywords.put("void",    TokenType.VOID);
        keywords.put("bool",    TokenType.BOOL);
        keywords.put("char",    TokenType.CHAR);
        keywords.put("decimal", TokenType.DECIMAL);
        keywords.put("int",     TokenType.INT);
        keywords.put("string",  TokenType.STRING);
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

        tokens.add(new Token(TokenType.EOF, "", null, line, start + 1));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                break;
            case '(': addToken(TokenType.LEFT_PAREN, "("); break;
            case ')': addToken(TokenType.RIGHT_PAREN, ")"); break;
            case '{': addToken(TokenType.LEFT_BRACE, "{"); break;
            case '}': addToken(TokenType.RIGHT_BRACE, "}"); break;
            case ',': addToken(TokenType.COMMA, ","); break;
            case '.': addToken(TokenType.DOT, "."); break;
            case ':': addToken(TokenType.COLON, ":"); break;
            case '^': addToken(TokenType.BITWISE_XOR, "^"); break;
            case ';': addToken(TokenType.SEMICOLON, ";"); break;
            case '~': addToken(TokenType.BITWISE_NOT, "~"); break;
            case '"': string(); break;
            case '\'': character(); break;
            case '&': 
                if(match('&'))      addToken(TokenType.AND, "&&");
                else if(match('=')) addToken(TokenType.AND_EQUAL, "&=");
                else                         addToken(TokenType.BITWISE_AND, "&");
                break;
            case '-':
                if(match('-'))      addToken(TokenType.DECREMENT, "--");
                else if(match('-')) addToken(TokenType.SUB_EQUAL, "-=");
                else                         addToken(TokenType.SUB, "-");
                break;
            case '+':
                if(match('+'))      addToken(TokenType.INCREMENT, "++");
                else if(match('=')) addToken(TokenType.ADD_EQUAL, "+=");
                else                         addToken(TokenType.ADD, "+");
                break;
            case '*': 
                if(match('*')) {
                    current++;
                    if(match('=')) addToken(TokenType.POWER_EQUAL, "**=");
                    else           addToken(TokenType.POWER, "**");
                }
                else if(match('=')) addToken(TokenType.MUL_EQUAL, "*=");
                else                         addToken(TokenType.MUL, "*");
                break;
            case '/':
                if (match('/'))
                    while (peek() != '\n' && !isEnd()) advance();
                else if(match('*'))
                    while((peek() != '*' && program.charAt(current + 1) != '/') && !isEnd()) advance();
                else if(match('='))addToken(TokenType.DIV_EQUAL, "/=");
                else                        addToken(TokenType.DIV, "/");
                break;
            case '%':
                if(match('=')) addToken(TokenType.MOD_EQUAL, "%=");
                else                    addToken(TokenType.MOD, "%");
                break;
            case '|':
                if(match('|')) addToken(TokenType.OR, "||");
                else                    addToken(TokenType.BITWISE_OR, "|");
                break;
            case '!':
                if(match('=')) addToken(TokenType.NOT_EQUAL, "!=");
                else                    addToken(TokenType.NOT, "!");
                break;
            case '=':
                if(match('=')) addToken(TokenType.EQUAL, "==");
                else                    addToken(TokenType.ASSIGNMENT, "=");
                break;
            case '<':
                if(match('=')) addToken(TokenType.LESS_EQUAL, "<="); 
                else                    addToken(TokenType.LESS_THAN, "<");
                break;
            case '>':
                if(match('=')) addToken(TokenType.GREATER_EQUAL, ">="); 
                else                    addToken(TokenType.GREATER_THAN, ">");
                break;
            case '\n':
                addToken(TokenType.NEWLINE);
                line++;
                break;
            
            default:
                if (isDigit(c)) number();
                else if (isAlpha(c)) identifier();
                else jil.error(line, "Unexpected character." + c);
            
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = program.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.VARIABLE;
        addToken(type, text);
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

        addToken(TokenType.CHAR_CONSTANT, value.charAt(0));
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

        if((long)d - d == 0)
            addToken(TokenType.INT_CONSTANT, (long)d);
        else
            addToken(TokenType.DECIMAL_CONSTANT, d);
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
        tokens.add(new Token(type, text, literal, line, start));
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