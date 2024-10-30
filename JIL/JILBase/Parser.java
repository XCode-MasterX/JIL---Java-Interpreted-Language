package JILBase;

import java.util.ArrayList;

import org.checkerframework.checker.units.qual.t;

import JILDataTypes.*;

public class Parser {
    private final jil interpreter;
    private final ArrayList<TokenType> validBinOps = new ArrayList<>();
    private final TokenType validUniOps = TokenType.NOT;

    public Parser(jil inter) { 
        this.interpreter = inter;

        validBinOps.add(TokenType.ADD); 
        validBinOps.add(TokenType.SUB); 
        validBinOps.add(TokenType.MUL); 
        validBinOps.add(TokenType.DIV); 
        validBinOps.add(TokenType.MOD);
        validBinOps.add(TokenType.BITWISE_AND);
        validBinOps.add(TokenType.BITWISE_OR);
        validBinOps.add(TokenType.BITWISE_XOR);
        validBinOps.add(TokenType.NOT_EQUAL);
    }

    public JILType parseExpression(final ArrayList<Token> tokens, int index) {
        JILType returnValue = null;
        TokenType currentType = tokens.get(index).type;

        if(currentType == validUniOps || currentType == TokenType.TRUE || currentType == TokenType.FALSE)
            returnValue = parseExpAsBoolean(tokens, index);
        else if(currentType == TokenType.INT_CONSTANT)             returnValue = parseExpAsInt(tokens, index);
        else if(currentType == TokenType.DECIMAL_CONSTANT)         returnValue = parseExpAsDecimal(tokens, index);
        else if(currentType == TokenType.CHAR_CONSTANT)            returnValue = parseExpAsChar(tokens, index);
        else if(currentType == TokenType.STRING_CONSTANT)          returnValue = parseExpAsString(tokens, index);
        
        return returnValue;
    }

    private JILInt parseExpAsInt(final ArrayList<Token> tokens, int index) {
        StringBuilder expression = new StringBuilder();
        long value = 0;
        Token current = tokens.get(index);

        while(current.type != TokenType.NEWLINE) {
            
            if(current.type == TokenType.VARIABLE)
                expression.append(interpreter.getVariableValue(current));
            else if(current.type == TokenType.INT_CONSTANT)
                expression.append(current.literal);
            else if(validBinOps.contains(current.type)) {
                final TokenType prevToken = tokens.get(index - 1).type;

                if(prevToken == TokenType.INT_CONSTANT || prevToken == TokenType.VARIABLE) {
                    final TokenType nextToken = getNext(tokens, index).type;
                    if(nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE)
                        expression.append(current.lexeme);
                    else
                        jil.error(current.line, "Expected next token of " + current.literal + " to be an int type.");
                }
                else
                    jil.error(current.line, "Expected previous token of " + current.lexeme + " to be an int type.");
            }
            else {
                jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);
            }

            current = tokens.get(++index);
        }

        System.out.println("Found Int Expression: " + expression.toString());
        return new JILInt(value, false);
    }

    private JILDecimal parseExpAsDecimal(final ArrayList<Token> tokens, int index) {
        StringBuilder expression = new StringBuilder();
        long value = 0;
        Token current = tokens.get(index);

        while(current.type != TokenType.NEWLINE) {
            
            if(current.type == TokenType.VARIABLE)
                expression.append(interpreter.getVariableValue(current));
            else if(current.type == TokenType.INT_CONSTANT || current.type == TokenType.DECIMAL_CONSTANT)
                expression.append(current.literal);
            else if(validBinOps.contains(current.type)) {
                final TokenType prevToken = tokens.get(index - 1).type;

                if(prevToken == TokenType.INT_CONSTANT || prevToken == TokenType.VARIABLE) {
                    final TokenType nextToken = getNext(tokens, index).type;
                    if(nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE)
                        expression.append(current.lexeme);
                    else
                        jil.error(current.line, "Expected next token of " + current.literal + " to be an int type.");
                }
                else
                    jil.error(current.line, "Expected previous token of " + current.lexeme + " to be an int type.");
            }
            else
                jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);

            current = tokens.get(++index);
        }

        System.out.println("Found Decimal Expression: " + expression.toString());
        return new JILDecimal(value, false);
    }

    private JILChar parseExpAsChar(final ArrayList<Token> tokens, int index) {
        StringBuilder expression = new StringBuilder();
        char value = '\0';
        Token current = tokens.get(index);

        while(current.type != TokenType.NEWLINE) {
            
            if(current.type == TokenType.VARIABLE)
                expression.append(interpreter.getVariableValue(current));
            else if(current.type == TokenType.INT_CONSTANT)
                expression.append(current.literal);
            else if(validBinOps.contains(current.type)) {
                final TokenType prevToken = tokens.get(index - 1).type;

                if(prevToken == TokenType.INT_CONSTANT || prevToken == TokenType.VARIABLE) {
                    final TokenType nextToken = getNext(tokens, index).type;
                    if(nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE)
                        expression.append(current.lexeme);
                    else
                        jil.error(current.line, "Expected next token of " + current.literal + " to be an int type.");
                }
                else
                    jil.error(current.line, "Expected previous token of " + current.lexeme + " to be an int type.");
            }
            else
                jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);

            current = tokens.get(++index);
        }

        System.out.println("Found Decimal Expression: " + expression.toString());
        return new JILChar(value, false);
    }

    private JILString parseExpAsString(final ArrayList<Token> tokens, int index) {
        StringBuilder expression = new StringBuilder();
        String value = "";
        Token current = tokens.get(index);

        while(current.type != TokenType.NEWLINE) {
            
            if(current.type == TokenType.VARIABLE)
                expression.append(interpreter.getVariableValue(current));
            else if(validBinOps.contains(current.type)) {
                final TokenType prevToken = tokens.get(index - 1).type;

                if(prevToken == TokenType.STRING_CONSTANT || prevToken == TokenType.VARIABLE) {
                    final TokenType nextToken = getNext(tokens, index).type;
                    if(nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE)
                        expression.append(current.lexeme);
                    else
                        jil.error(current.line, "Expected next token of " + current.literal + " to be an int type.");
                }
                else
                    jil.error(current.line, "Expected previous token of " + current.lexeme + " to be an int type.");
            }
            else
                expression.append(current.literal);

            current = tokens.get(++index);
        }

        System.out.println("Found Decimal Expression: " + expression.toString());
        return new JILString(value, false);
    }

    private JILBoolean parseExpAsBoolean(final ArrayList<Token> tokens, int index) {
        StringBuilder expression = new StringBuilder();
        boolean value = true;
        Token current = tokens.get(index);

        while(current.type != TokenType.NEWLINE) {
            
            if(current.type == TokenType.VARIABLE)
                expression.append(interpreter.getVariableValue(current));
            else if(current.type == TokenType.TRUE || current.type == TokenType.FALSE)
                expression.append(current.literal);
            else if(validBinOps.contains(current.type)) {
                final TokenType prevToken = tokens.get(index - 1).type;

                if(prevToken == TokenType.INT_CONSTANT || prevToken == TokenType.VARIABLE) {
                    final TokenType nextToken = getNext(tokens, index).type;
                    if(nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE)
                        expression.append(current.lexeme);
                    else
                        jil.error(current.line, "Expected next token of " + current.literal + " to be an int type.");
                }
                else
                    jil.error(current.line, "Expected previous token of " + current.lexeme + " to be an int type.");
            }
            else
                jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);

            current = tokens.get(++index);
        }

        System.out.println("Found Decimal Expression: " + expression.toString());
        return new JILBoolean(value, false);
    }

    public boolean parseBooleanExpression(final String expression) {

        return false;
    }

    private Token getNext(final ArrayList<Token> tokens, final int index) { return tokens.get(index + 1); }
}