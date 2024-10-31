package JILBase;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;
    final int index;
  
    Token(TokenType type, String lexeme, Object literal, int line, int index) {
      this.type = type;
      this.lexeme = lexeme;
      this.literal = literal;
      this.line = line;
      this.index = index;
    }
    
    public String toString() {
      return "Line: " + line + " -> " + lexeme;
    }
}