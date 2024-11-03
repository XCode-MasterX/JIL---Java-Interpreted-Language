package JILBase;

import java.util.ArrayList;

import JILDataTypes.JILType;

public class Statement {
    private ArrayList<Token> content;
    public static final ArrayList<TokenType> operators;
    public static final ArrayList<TokenType> shortHand;

    static {
        operators = new ArrayList<>();
        shortHand = new ArrayList<>();

        operators.add(TokenType.ADD);
        operators.add(TokenType.SUB);
        operators.add(TokenType.MUL);
        operators.add(TokenType.DIV);
        operators.add(TokenType.MOD);
        operators.add(TokenType.AND);
        operators.add(TokenType.OR);
        operators.add(TokenType.BITWISE_XOR);
        operators.add(TokenType.BITWISE_AND);
        operators.add(TokenType.BITWISE_OR);
        operators.add(TokenType.BITWISE_NOT);
        operators.add(TokenType.NOT);

        shortHand.add(TokenType.ADD_EQUAL);
        shortHand.add(TokenType.SUB_EQUAL);
        shortHand.add(TokenType.MUL_EQUAL);
        shortHand.add(TokenType.DIV_EQUAL);
        shortHand.add(TokenType.MOD_EQUAL);
        shortHand.add(TokenType.AND_EQUAL);
        shortHand.add(TokenType.OR_EQUAL);
    }

    public Statement() { 
        content = new ArrayList<>();
    }

    public void addToken(Token tok) { content.add(tok); }

    public void breakdown() {
        if(content.get(0).type != TokenType.VARIABLE) {
            jil.error(content.get(0).line, "Statements are supposed to start with a variable or function.");
            return;
        }
        
        if(content.get(1).type == TokenType.VARIABLE) {
            jil.error(content.get(0).line, "Can't have 2 variables side by side without an operator.");
            return;
        }

        Expression exp = new Expression();
        exp.createExpression(content, 0, 0);
    }

    public boolean isEmpty() { return content.size() == 0 && toString().trim().length() != 0; }
    public String toString() { 
        StringBuilder s = new StringBuilder();

        for(Token t : content) s.append(t.viewString() + " ");

        return s.toString() + "\n";
    }
}