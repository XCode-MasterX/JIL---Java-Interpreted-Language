package JILBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import JILDataTypes.JILType;
import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;

public class Program {
    private Variable variable[];
    private final ArrayList<Block> codeBlocks;
    private final ArrayList<Token> userProgram;
    private final HashMap<String, Program> importedPrograms;

    public Program(ArrayList<Token> program) {
        userProgram = program;
        codeBlocks = new ArrayList<>();
        importedPrograms = new HashMap<>();
    }

    public void run() {
        for(int i = 0; i < userProgram.size(); i++) {
            Token tok = userProgram.get(i);

            switch(tok.type) {
                case TokenType.IMPORT: {
                    i = handleImports(i);
                    break;
                }

                case TokenType.DECLARE: {
                    i = handleDeclare(i);
                    break;
                }

                case TokenType.INITIAL: {
                    i = handleInitial(i);
                    break;
                }

                case TokenType.DRIVER: {
                    i = createDriverBlock(i);
                    break;
                }

                case TokenType.VARIABLE: {
                    i = createFunctionBlock(i);
                    break;
                }

                case TokenType.NEWLINE:{break;}

                default: {
                    jil.error(tok.line, "Index: " + i + " Found unexpected token (" + tok + ") outside a block. [Error in Program.run()]");
                    break;
                }
            }
        }
    }

    private int handleImports(int index) {
        while(userProgram.get(index).type != TokenType.LEFT_BRACE) index++;

        Token currToken = userProgram.get(index);
        while(currToken.type != TokenType.RIGHT_BRACE) {
            if(currToken.type == TokenType.STRING_CONSTANT) System.out.println("Found import: " + currToken.literal);
            currToken = userProgram.get(index++);
        }

        return index;
    }

    public int handleDeclare(int index) {
        if(userProgram.get(index + 1).type != TokenType.LEFT_BRACE)
            jil.error(userProgram.get(index).line, "Expected { found " + userProgram.get(index));
        
        Token tok;
        HashMap<String, TokenType> var = new HashMap<>();
        HashMap<String, Boolean> isConst = new HashMap<>();
        int arrIndex = 0;
        
        final ArrayList<TokenType> dataList = new ArrayList<>();
        dataList.add(TokenType.INT);
        dataList.add(TokenType.DECIMAL);
        dataList.add(TokenType.BOOL);
        dataList.add(TokenType.CHAR);
        dataList.add(TokenType.STRING);
        
        for(; userProgram.get(index).type != TokenType.RIGHT_BRACE; index++) {          
            int inc = 0;
            boolean constant = false;
            
            if(userProgram.get(index).type == TokenType.VARIABLE) {
                inc++;
                tok = userProgram.get(index + inc);
                if(!(tok.type == TokenType.COLON))
                jil.error(tok.line, "Expected ':' found " + tok);
                
                inc++;
                tok = userProgram.get(index + inc);
                if(tok.type == TokenType.CONST) {
                    inc++;
                    tok = userProgram.get(index + inc);
                    
                    if(!dataList.contains(tok.type)) jil.error(tok.line, "Found CONST but, expected data type, found " + tok);
                    constant = true;
                }
                else if(!dataList.contains(tok.type))
                jil.error(tok.line, "Expected data type, found " + tok);
                
                var.put(userProgram.get(index).lexeme, tok.type);
                isConst.put(userProgram.get(index).lexeme, constant);
            }
        }
        
        ArrayList<Map.Entry<String, TokenType>> sortable = new ArrayList<>(var.entrySet());        
        Collections.sort(sortable, (x, y) -> x.getKey().compareTo(y.getKey()));

        JILType val = null;
        variable = new Variable[sortable.size()];
        
        for(Map.Entry<String, TokenType> ele : sortable)
        {
            TokenType type = ele.getValue();

            if(isConst.get(ele.getKey()))
                val = (JILType) jil.objectConstructor[type.ordinal()].constructConstant();
            else
                val = (JILType) jil.objectConstructor[type.ordinal()].constructDefault();

            variable[arrIndex++] = new Variable(ele.getKey(), val);
        }

        return index;
    }

    private int handleInitial(int index) {
        if(userProgram.get(index + 1).type != TokenType.LEFT_BRACE)
            jil.error(userProgram.get(index).line, "Expected { found " + userProgram.get(index));

        Token curToken = null;

        while((curToken = userProgram.get(index)).type != TokenType.RIGHT_BRACE) {
            if(curToken.type == TokenType.VARIABLE) {
                index = handleAssignment(index);
                continue;
            }
            index++;
        }
        System.out.println("Variable names: " + Arrays.toString(variable));

        return index;
    }

    private int handleAssignment(int index) {
        Token curToken = userProgram.get(index);
        JILType curVariable = getVariableValue(curToken);

        try {
            while((curToken = userProgram.get(index)).type != TokenType.NEWLINE && curToken.type != TokenType.SEMICOLON) {
                Token tok;
                int inc = 0;

                if(curToken.type == TokenType.VARIABLE) {
                    curVariable = getVariableValue(curToken);

                    inc++;
                    tok = userProgram.get(index + inc);
                    if(tok.type != TokenType.ASSIGNMENT)
                        jil.error(tok.line, "Expected '=', but found: " + tok.lexeme);

                    inc++;
                    tok = userProgram.get(index + inc);

                    if(tok.type == TokenType.VARIABLE) {
                        if(getVariableValue(tok).wasSet) {
                            JILType value = jil.parser.parseExpression(getVariableValue(curToken), userProgram, index + inc);
                            curVariable.setValue(value.getValue(tok.line), tok.line);
                            while(userProgram.get(index++).type != TokenType.NEWLINE);
                            continue;
                        }
                        checkCompatible(getVariableValue(curToken).dataType, getVariableValue(tok).dataType, curToken.line);
                        index = handleAssignment(index + inc);
                        curVariable.setValue(getVariableValue(tok).getValue((short) curToken.line), (short) curToken.line);
                        continue;
                    }
                    else {
                        JILType value = jil.parser.parseExpression(getVariableValue(curToken), userProgram, index + inc);
                        curVariable.setValue(value.getValue(tok.line), tok.line);

                        while(userProgram.get(index++).type != TokenType.NEWLINE);

                        return index;
                    }
                }
                index++;
            }
        }
        catch(WrongCastException e) {
            jil.error(e.line, e.getMessage());
        }
        catch(ConstantValueEditException e) {
            jil.error(e.line, e.getMessage());
        }
        catch(ValueNotSetException e) {
            jil.error(e.line, e.getMessage());
        }

        return index;
    }

    private int createDriverBlock(int index) {
        Block block = new Block();
        index = block.createBlock(userProgram, index + 2, 0)[0];
        codeBlocks.add(block);

        System.out.println(block);
        return index;
    }

    private int createFunctionBlock(int index) {
        while(userProgram.get(index).type != TokenType.LEFT_BRACE) index++;

        Block block = new Block();
        index = block.createBlock(userProgram, index + 1, 0)[0];
        codeBlocks.add(block);

        System.out.println(block);
        return index;
    }

    public int getVariable(final String search) {
        int start = 0, end = variable.length - 1, mid = (start + end) / 2;
        
        while(start <= end) {
            mid = (start + end) / 2;

            if(mid > end) break;

            if(search.equals(variable[mid].name)) return mid;

            if(variable[mid].name.compareTo(search) < 0) start = mid + 1;
            else if(variable[mid].name.compareTo(search) > 0) end = mid - 1;
        }

        return -1;
    }

    public JILType getVariableValue(final Token search) {
        int index = getVariable(search.lexeme);

        if(index != -1) return variable[index].getValue();
        
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
}