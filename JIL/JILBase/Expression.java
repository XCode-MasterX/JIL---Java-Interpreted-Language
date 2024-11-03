package JILBase;

import java.util.ArrayList;
import java.util.Stack;

import JILDataTypes.JILType;
import JILExceptions.ValueNotSetException;

public class Expression {
    private ArrayList<Object> expression;
    private boolean reEvaluate = false;
    private int line;
    private JILType returnValue;

    public Expression() {
        expression = new ArrayList<>();
        returnValue = null;
    }

    public int[] createExpression(final ArrayList<Token> statement, int index, int depth) {
        line = statement.get(index).line;
        Token current = null;

        for(; index < statement.size(); index++) {
            current = statement.get(index);

            if(current.type == TokenType.LEFT_PAREN) {
                Expression sub = new Expression();

                int ret[] = sub.createExpression(statement, index, depth + 1);
                index = ret[0];
                if(ret[1] != depth) jil.error(current.line, "There are unterminated parenthesis.");
                
                reEvaluate |= sub.reEvaluate;
                expression.add(sub);
                continue;
            }

            if(current.type == TokenType.RIGHT_PAREN) break;

            reEvaluate |= current.type == TokenType.VARIABLE;
            expression.add(current);
        }

        return new int[]{index, depth - 1};
    }

    public JILType getEvaluation() throws ValueNotSetException{
        if(!reEvaluate && returnValue != null) return returnValue;

        expression = toPostfix(expression);
        Stack<JILType> stack = new Stack<>();

        for (Object part : expression) {
            if (part instanceof JILType x) {
                stack.push(x);
            }
            else if(part instanceof Expression exp) {
                stack.push(exp.getEvaluation());
            }
            else if (part instanceof String) {
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

        returnValue = stack.pop();
        return returnValue;
    }

    private ArrayList<Object> toPostfix(ArrayList<Object> expression) {
        Stack<String> operatorStack = new Stack<>();
        ArrayList<Object> output = new ArrayList<>();
    
        for (Object element : expression) {
            TokenType tokenType = getType(element);
            if (tokenType == TokenType.VARIABLE)
                output.add(element);
            else if (Statement.operators.contains(tokenType)) {
                while (!operatorStack.isEmpty() && hasHigherPrecedence(operatorStack.peek(), element.toString()))
                    output.add(operatorStack.pop());

                operatorStack.push(element.toString());
            }
        }
    
        while (!operatorStack.isEmpty()) output.add(operatorStack.pop());
    
        return output;
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
}