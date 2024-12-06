package JILBase;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import JILDataTypes.JILType;

public class Program implements StoreVariables {
    Variable variable[];
    private Block driverBlock;
    final ArrayList<Token> userProgram;
    private HashMap<String, UserFunction> function;
    private final HashMap<String, Program> importedPrograms;
    private final HashMap<String, Callable> libImports;

    public Program(ArrayList<Token> program) {
        userProgram = program;
        function = new HashMap<>();
        importedPrograms = new HashMap<>();
        libImports = new HashMap<>();
    }

    public void run() { driverBlock.run(driverBlock.startPoint); }

    public void initialize() {
        if(getLength() == 0) jil.error(-1, "Can't initialize an empty program.");
        
        int i = 0;
        Token tok = userProgram.get(i);
        
        while((tok = userProgram.get(i)).type != TokenType.EOF) {
            switch(tok.type) {
                case TokenType.IMPORT: {
                    i = handleImports(i);
                    break;
                }

                case TokenType.DECLARE: {
                    final ArrayList<TokenType> finishers = new ArrayList<>();
                    finishers.add(TokenType.RIGHT_BRACE);
                    ReturnStructure<Integer, Variable[]> x = jil.handleDeclaration(userProgram, i, finishers);
                    i = x.x;
                    variable = x.y;
                    break;
                }
                
                case TokenType.INITIAL: {
                    i = jil.handleInitial(this, i);
                    break;
                }
                
                case TokenType.DRIVER: {
                    i = createDriverBlock(i);
                    break;
                }

                case TokenType.VARIABLE: {
                    if(function == null) function = new HashMap<>();
                    i = createFunctionBlock(i);
                    break;
                }

                case TokenType.NEWLINE:{ break; }

                default: {
                    jil.error(tok.line, "Index: " + i + " Found unexpected token (" + tok + ") outside a block. [Error in Program.run()]");
                    break;
                }
            }
            i++;
        }

        driverBlock.breakdown();
        for(UserFunction func : function.values()) func.breakdown();
    }

    // WORKS, for what it does.
    private int handleImports(int index) {
        while(userProgram.get(index).type != TokenType.LEFT_BRACE) index++;

        Token currToken = userProgram.get(index);
        while(currToken.type != TokenType.RIGHT_BRACE) {
            if(currToken.type == TokenType.STRING_CONSTANT) {                 
                String refName = "";
                if(userProgram.get(index++).type == TokenType.AS && userProgram.get(index).type == TokenType.VARIABLE)
                    refName = userProgram.get(index).lexeme;
                else
                    jil.error(currToken.line, "Excuse what are you trying to write? Found '" + userProgram.get(index).lexeme + "'. Here's an example to help: \"example.jil\" as expl");

                Program imported = new Program(jil.createTokens(jil.readFile(currToken.literal.toString())));

                if(imported.getLength() == 0)
                    jil.logger.writef("[%d] %s", currToken.line, "Just read through the file... Why import an empty program??");

                importedPrograms.put(refName, imported);
                index++;
            }
            else if(currToken.type == TokenType.VARIABLE) {
                String refName = "";
                if(userProgram.get(index++).type == TokenType.AS && userProgram.get(index).type == TokenType.VARIABLE)
                    refName = userProgram.get(index).lexeme;
                else
                    jil.error(currToken.line, "Excuse what are you trying to write? Found '" + userProgram.get(index).lexeme + "'. Here's an example to help: jilio as io");
                
                libImports.put(refName, jil.getLibraryObject(currToken.line, currToken.lexeme));
                System.out.println("Imported Successfully as: " + refName);
                index++;
            }

            currToken = userProgram.get(index++);
        }

        final Token ctok = currToken;
        for(Callable x : libImports.values())
            x.call("writef", ctok.line, 1, 2, 3, 4);

        return index;
    }

    // WORKS
    private int createDriverBlock(int index) {
        driverBlock = new Block(0, this);
        index = driverBlock.createBlock(userProgram, index + 2, 0)[0];
        return index;
    }

    // WORKS
    private int createFunctionBlock(int index) {
        String funcName = userProgram.get(index).lexeme;
        UserFunction func = new UserFunction(funcName, this);
        index = func.createArgs(this, index + 1);
        index = func.createReturnType(this, index + 1);
        index = func.createInternalVariables(this, index + 1);
        index = func.createBlock(this, index + 1);

        function.put(funcName, func);
        return index;
    }

    public int getLength() {
        int count = 0;

        for(Token t : userProgram) 
            if(t.type != TokenType.NEWLINE && t.lexeme.length() != 0) count++;

        return count;
    }

    public JILType getVariable(final String search) {
        for(int start = 0; start < variable.length; start++)
            if(search.equals(variable[start].name))
                return variable[start].getValue();

        return null;
    }

    public UserFunction getFunction(final String search) { return function.getOrDefault(search, null); }

    public Program getImported(final String search) { return importedPrograms.getOrDefault(search, null); }

    public Callable getLibrary(final String search) { return libImports.getOrDefault(search, null); }

    public Object getVariableValue(final Token search) {
        if(search.type != TokenType.VARIABLE) {
            System.out.println(search.viewString() + " is not a variable name.");
            return null;
        }

        Object returnable = getVariable(search.lexeme);
        returnable = returnable == null ? getFunction(search.lexeme) : returnable;
        returnable = returnable == null ? getImported(search.lexeme) : returnable;
        returnable = returnable == null ? getLibrary(search.lexeme) : returnable;
        
        if(returnable != null) return returnable;
        
        jil.error(search.line, "The variable " + search.lexeme + " has not been defined.");
        return null;
    }

    public void checkCompatible(final TokenType a, final TokenType b, final int line) {
        if( a == b
            || (a == TokenType.STRING)
            || (a == TokenType.DECIMAL && (b == TokenType.INT || b == TokenType.CHAR || b == TokenType.BOOL))
            || (a == TokenType.INT && (b == TokenType.CHAR || b == TokenType.BOOL)))
            return;
        else
            jil.error(line, "The data types are incompatible.");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Variables: ");
        if(variable == null)
            sb.append("[No variables]\n");
        else
            Arrays.stream(variable).forEach((x) -> sb.append(x));

        sb.append("Functions: ");
        if(function != null && function.size() != 0)
            sb.append(Arrays.toString(function.values().toArray()));
        else
            sb.append("[No functions]");
        
        return sb.toString();
    }
}