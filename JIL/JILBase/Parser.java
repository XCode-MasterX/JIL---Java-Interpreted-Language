package JILBase;

import java.util.ArrayList;
import java.util.Stack;

import JILDataTypes.*;
import JILExceptions.ValueNotSetException;

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

    public JILType parseExpression(final JILType variable, final ArrayList<Token> tokens, int index) {
        JILType returnValue = null;

        if(variable instanceof JILBoolean)
            returnValue = parseExpAsBoolean(tokens, index);
        else if(variable instanceof JILInt)             returnValue = parseExpAsInt(tokens, index);
        else if(variable instanceof JILDecimal)         returnValue = parseExpAsDecimal(tokens, index);
        else if(variable instanceof JILChar)            returnValue = parseExpAsChar(tokens, index);
        else if(variable instanceof JILString)          returnValue = parseExpAsString(tokens, index);
        
        return returnValue;
    }

    private ArrayList<Object> convertToPostfix(ArrayList<Object> expression) {
        Stack<String> operatorStack = new Stack<>();
        ArrayList<Object> output = new ArrayList<>();
    
        for (Object element : expression) {
            TokenType tokenType = getType(element);
            if (tokenType == TokenType.VARIABLE)
                output.add(element);
            else if (validBinOps.contains(tokenType)) {
                while (!operatorStack.isEmpty() && hasHigherPrecedence(operatorStack.peek(), element.toString()))
                    output.add(operatorStack.pop());

                operatorStack.push(element.toString());
            }
        }
    
        while (!operatorStack.isEmpty()) output.add(operatorStack.pop());
    
        return output;
    }

    private JILType evaluate(ArrayList<Object> expression, final int line) throws ValueNotSetException{
        Stack<JILType> stack = new Stack<>();
        for (Object part : expression) {
            if (part instanceof JILType x) {
                // Push the integer value onto the stack
                stack.push(x);
            } else if (part instanceof String) {
                // It's an operator; pop the top two values from the stack
                JILType operand2 = stack.pop();
                JILType operand1 = stack.pop();
            
                switch (part.toString()) {
                    case "+":
                        operand1.add(operand2, line);
                        break;
                    case "-":
                        operand1.sub(operand2, line);
                        break;
                    case "*":
                        operand1.mul(operand2, line);
                        break;
                    case "**":
                        operand1.power(operand2, line);
                        break;
                    case "/":
                        if ((Long) operand2.getValue(line) == 0)
                            jil.error(line, "Division by zero error.");
                        operand1.div(operand2, line);
                        break;
                    case "%":
                        if ((Long) operand2.getValue(line) == 0)
                            jil.error(line, "Modulo by zero error.");
                        operand1.mod(operand2, line);
                        break;
                    case "|":
                        operand1.or(operand2, line);
                        break;
                    case "&":
                        operand1.and(operand2, line);
                        break;
                    case "^":
                        operand1.xor(operand2, line);
                        break;
                    default:
                        jil.error(line, "Unsupported operator: " + part);
                        break;
                }
                stack.push(operand1);
            }
        }

        return stack.pop();
    }

    private JILInt parseExpAsInt(final ArrayList<Token> tokens, int index) {
        ArrayList<Object> expression = new ArrayList<>();
        Token current = tokens.get(index);
        final int line = current.line;

        while(current.type != TokenType.NEWLINE) {            
            switch(current.type) {
                case TokenType.VARIABLE:
                    expression.add(interpreter.currentProgram.getVariableValue(current).getCopy());
                    break;
                case TokenType.INT_CONSTANT:
                    expression.add(new JILInt(Long.parseLong(current.lexeme), false));
                    break;
                default:
                    if(validBinOps.contains(current.type)) {
                        final TokenType prevToken = tokens.get(index - 1).type;
                        final TokenType nextToken = getNext(tokens, index).type;

                        if(prevToken == TokenType.INT_CONSTANT || prevToken == TokenType.VARIABLE &&
                           nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE)
                                expression.add(current.lexeme);
                        else
                            jil.error(current.line, "Expected value around '" + current.lexeme + "' to be an int type.");
                    }
                    else
                        jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);
            }

            current = tokens.get(++index);
        }

        expression = convertToPostfix(expression);
        JILInt returnValue = null;

        try {
            returnValue = (JILInt) evaluate(expression, line);
        }
        catch(ValueNotSetException e) {
            e.printStackTrace();
            jil.error(line, e.getMessage());
        }
        
        return returnValue;
    }

    private JILDecimal parseExpAsDecimal(final ArrayList<Token> tokens, int index) {
        ArrayList<Object> expression = new ArrayList<>();
        Token current = tokens.get(index);
        final int line = current.line;

        while(current.type != TokenType.NEWLINE) {            
            switch(current.type) {
                case TokenType.VARIABLE:
                    expression.add(interpreter.getVariableValue(current).getCopy());
                    break;
                case TokenType.INT_CONSTANT:
                case TokenType.DECIMAL_CONSTANT:
                    expression.add(new JILDecimal(Double.parseDouble(current.lexeme), false));
                    break;
                default:
                    if(validBinOps.contains(current.type)) {
                        final TokenType prevToken = tokens.get(index - 1).type;
                        final TokenType nextToken = getNext(tokens, index).type;

                        if(prevToken == TokenType.INT_CONSTANT || prevToken == TokenType.VARIABLE || prevToken == TokenType.DECIMAL_CONSTANT &&
                           nextToken == TokenType.INT_CONSTANT || nextToken == TokenType.VARIABLE || nextToken == TokenType.DECIMAL_CONSTANT)
                                expression.add(current.lexeme);
                        else
                            jil.error(current.line, "Expected value around '" + current.lexeme + "' to be an int type.");
                    }
                    else
                        jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);
            }

            current = tokens.get(++index);
        }

        expression = convertToPostfix(expression);
        JILDecimal returnValue = null;

        try {
            returnValue = (JILDecimal) evaluate(expression, line);
        }
        catch(ValueNotSetException e) {
            e.printStackTrace();
            jil.error(line, e.getMessage());
        }
        
        return returnValue;
    }

    private JILChar parseExpAsChar(final ArrayList<Token> tokens, int index) {
        ArrayList<Object> expression = new ArrayList<>();
        Token current = tokens.get(index);
        final int line = current.line;

        while(current.type != TokenType.NEWLINE) {            
            switch(current.type) {
                case TokenType.VARIABLE:
                    expression.add(interpreter.getVariableValue(current).getCopy());
                    break;
                case TokenType.CHAR_CONSTANT:
                    expression.add(new JILChar(current.lexeme.charAt(1), false));
                    break;
                default:
                    if(validBinOps.contains(current.type)) {
                        final TokenType prevToken = tokens.get(index - 1).type;
                        final TokenType nextToken = getNext(tokens, index).type;

                        if(prevToken == TokenType.CHAR_CONSTANT || prevToken == TokenType.VARIABLE &&
                           nextToken == TokenType.CHAR_CONSTANT || nextToken == TokenType.VARIABLE)
                                expression.add(current.lexeme);
                        else
                            jil.error(current.line, "Expected value around '" + current.lexeme + "' to be an int type.");
                    }
                    else
                        jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);
            }

            current = tokens.get(++index);
        }

        expression = convertToPostfix(expression);
        JILChar returnValue = null;

        try {
            returnValue = (JILChar) evaluate(expression, line);
        }
        catch(ValueNotSetException e) {
            e.printStackTrace();
            jil.error(line, e.getMessage());
        }
        
        return returnValue;
    }

    private JILString parseExpAsString(final ArrayList<Token> tokens, int index) {
        ArrayList<Object> expression = new ArrayList<>();
        Token current = tokens.get(index);
        final int line = current.line;

        while(current.type != TokenType.NEWLINE) {            
            switch(current.type) {
                case TokenType.VARIABLE:
                    expression.add(interpreter.getVariableValue(current).getCopy());
                    break;
                case TokenType.INT_CONSTANT:
                    expression.add(new JILInt(Long.parseLong(current.lexeme), false));
                    break;
                case TokenType.STRING_CONSTANT:
                    expression.add(new JILString(current.lexeme.replace("\"", ""), false));
                    break;
                default:
                    if(validBinOps.contains(current.type))
                        expression.add(current.lexeme);
                    else
                        jil.error(current.line, "Unrecognised symbol used here: " + current.lexeme);
            }

            current = tokens.get(++index);
        }

        expression = convertToPostfix(expression);
        JILString returnValue = null;

        try {
            returnValue = (JILString) evaluate(expression, line);
        }
        catch(ValueNotSetException e) {
            e.printStackTrace();
            jil.error(line, e.getMessage());
        }
        
        return returnValue;
    }

    private JILBoolean parseExpAsBoolean(final ArrayList<Token> tokens, int index) {
        ArrayList<Token> expression = new ArrayList<>();
        boolean value = true;
        Token current = tokens.get(index);

        System.out.println("Found Boolean Expression: " + expression.toString());
        return new JILBoolean(value, false);
    }

    public boolean parseBooleanExpression(final String expression) {

        return false;
    }

    private boolean hasHigherPrecedence(String op1, String op2) {
        int precedence1 = getPrecedence(op1);
        int precedence2 = getPrecedence(op2);
        
        if (precedence1 == precedence2) {
            // If operators have equal precedence, give precedence to left-associative operators
            return !isRightAssociative(op1);
        }
        return precedence1 > precedence2;
    }
    
    private int getPrecedence(String operator) {
        switch (operator) {
            case "+": case "-": return 1;
            case "*": case "/": return 2;
            case "**": return 3;
            case "^": case "!": case "|": case "&": case "~": return 4;
            default: return -1;
        }
    }
    
    private boolean isRightAssociative(String operator) {
        return operator.equals("^");
    }

    private TokenType getType(Object token) {
        TokenType returnType = TokenType.INT_CONSTANT;

        if(token instanceof String)
            returnType = TokenType.ADD;
        else if(token instanceof JILType)
            returnType = TokenType.VARIABLE;

        return returnType;
    }

    private Token getNext(final ArrayList<Token> tokens, final int index) { return tokens.get(index + 1); }
}