package JILBase;

import java.util.ArrayList;

import JILDataTypes.JILType;

public class UserFunction implements StoreVariables{
    final String funcName;
    private Block funcBlock;
    private Variable arguments[];
    private Variable internalVariable[];
    private JILType returnType = null;
    private final StoreVariables owner;

    public UserFunction(String name, final StoreVariables owner) { 
        funcName = name;
        this.owner = owner;
    }

    public int createArgs(final Program progObj, int index) {
        // ONLY FOR CHECKING FORMAT.
        {
            final Token firstToken = progObj.userProgram.get(index);
            if(firstToken.type != TokenType.LEFT_PAREN)
                jil.error(firstToken.line, "Expected '(', but found " + firstToken.lexeme);
        }
        
        final ArrayList<TokenType> finishers = new ArrayList<>();
        finishers.add(TokenType.RIGHT_PAREN);
        ReturnStructure<Integer, Variable[]> x = jil.handleDeclaration(progObj.userProgram, index, finishers);
        arguments = x.y;

        return x.x;
    }

    public int createReturnType(final Program progObj, int index) {
        ArrayList<Token> program = progObj.userProgram;
        Token currToken = program.get(index);

        while((currToken = program.get(index)).type != TokenType.LEFT_BRACE) {
            if(currToken.type == TokenType.COLON && returnType == null) {
                if(!jil.dataList.contains(program.get(index + 1).type))
                    jil.error(currToken.line, "Index: " + index + " Found unexpected token during determining return-type (" + currToken + ") [Error in UserFunction.createBody()]");
                else {
                    returnType = (JILType) jil.objectConstructor[program.get(index + 1).type.ordinal()].constructDefault();
                    index++;
                }
            }
            else if(returnType != null)
                jil.error(currToken.line, "Found possibly a second return type for the same. Not allowed.");
        
            index++;
        }
        
        return index;
    }

    public int createInternalVariables(final Program progObj, int index) {
        final ArrayList<Token> program = progObj.userProgram;
        while(program.get(index).type != TokenType.TEMP) index++;

        final ArrayList<TokenType> finishers = new ArrayList<>();
        finishers.add(TokenType.RIGHT_BRACE);
        ReturnStructure<Integer, Variable[]> x = jil.handleDeclaration(progObj.userProgram, index, finishers);
        internalVariable = x.y;

        return x.x;
    }

    public int createBlock(final Program progObj, int index) {
        funcBlock = new Block(0, this);
        index = funcBlock.createBlock(progObj.userProgram, index, 0)[0];

        return index;
    }

    public void breakdown() { funcBlock.breakdown(); }

    public Object call(final int line, Object ...args) {
        if(arguments.length != args.length) {
            jil.error(line, "The no. of arguments passed is not the same as the amount of given.");
        }
        return null;
    }

    public JILType getVariableValue(final Token search) {
        JILType returnable = getVariable(search.lexeme);
        returnable = returnable == null ? owner.getVariable(search.lexeme) : returnable;

        return returnable;
    } 

    public JILType getVariable(final String search) {
        for(int start = 0; start < internalVariable.length; start++)
            if(search.equals(internalVariable[start].name))
                return internalVariable[start].getValue();

        for(int start = 0; start < arguments.length; start++)
            if(search.equals(arguments[start].name))
                return arguments[start].getValue();

        return null;
    }

    public String toString() {
        return String.format("%s -> {Arguments: %s, Internal Variables: %s}", 
                funcName,
                java.util.Arrays.toString(arguments), 
                java.util.Arrays.toString(internalVariable)
            );
    }
}