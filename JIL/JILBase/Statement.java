package JILBase;

import java.util.ArrayList;

import JILDataTypes.JILType;

public class Statement {
    private final Block owner;
    private final ArrayList<Token> content;
    private final ArrayList<Object> format;
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

    public Statement(final Block owner) {
        this.owner = owner;
        content = new ArrayList<>();
        format = new ArrayList<>();
    }

    public void addToken(Token tok) { if(tok.type != TokenType.NEWLINE) content.add(tok); }

    public void breakdown() {
        //if(content.get(0).type != TokenType.VARIABLE) {
        //    System.out.println(content);
        //    jil.error(content.get(0).line, "Statements are supposed to start with a variable or function.");
        //    return;
        //}

        Object startSym = owner.getVariableValue(content.get(0));
        if(startSym == null) startSym = jil.currentProgram.getVariableValue(content.get(0));
        if(startSym == null) return;
        
        //if(content.get(1).type == TokenType.VARIABLE) {
        //    jil.error(content.get(0).line, "Can't have 2 variables side by side without an operator.");
        //    return;
        //}

        if(startSym instanceof UserFunction) establishFunctionCall();
        else if(startSym instanceof JILType) establishVariableOperation();
        else {
            System.out.println(startSym.getClass());
        }
    }

    public void run(final int line) {        
        if(content.size() == 1) { 
            System.out.println(jil.currentProgram.getVariableValue(content.get(0))); 
            return;
        }
        
        try {
            Token firstTok = content.get(0);

            if(firstTok.type == TokenType.VARIABLE) {
                Object variable = owner.getVariableValue(content.get(0));
                System.out.println(variable);
            }
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    private void establishFunctionCall() {
        System.out.println("Function call setup");
        final ArrayList<TokenType> delimiter = new ArrayList<>();
        delimiter.add(TokenType.COMMA);
        delimiter.add(TokenType.RIGHT_PAREN);

        format.add(jil.currentProgram.getVariableValue(content.get(0)));
        for(int i = 1; i < content.size() && content.get(i).type != TokenType.RIGHT_BRACE; i++) {
            if(content.get(i).type == TokenType.LEFT_PAREN) {
                Expression x = new Expression(owner);
                i = x.createExpression(content, i + 1, 0, delimiter)[0];
                format.add(x);
            }
        }
    }

    private void establishVariableOperation() {
        System.out.println("Variable operation...");
    }

    public boolean isEmpty() { return content.size() == 0; }

    public String toString() { 
        StringBuilder s = new StringBuilder();

        for(Token t : content) s.append(t.viewString() + " ");

        return s.toString() + "\n";
    }
}