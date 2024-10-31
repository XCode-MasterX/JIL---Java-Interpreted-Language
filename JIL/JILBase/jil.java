package JILBase;

import JILDataTypes.*;
import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILIO.IO;
import JILUtils.JILCaster;
import JILUtils.JILConstructor;
import JILUtils.JILFunction;
import JILUtils.JILUserFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class jil {
    @SuppressWarnings("rawtypes")
    private HashMap<String, JILUserFunction> userFunctions = null;
    private static String[] varNames;
    private static JILType[] varValues;
    private static JILCaster[] varCaster;
    private ArrayList<Token> userProgram;

    private final String[] libraryNames = {"jilio", "jilutils"};
    private HashMap<String, Object> libraries;

    public static IO logger = null;
    private Parser parser;
    private static boolean hadError = false;
    private final JILCaster objectCaster[];
    private JILConstructor objectConstructor[];

    public jil(final String userFile, final String logFile) {
        objectCaster = new JILCaster[]{
            new JILCaster<JILBoolean>(JILBoolean.class),
            new JILCaster<JILChar>(JILChar.class),
            new JILCaster<JILDecimal>(JILDecimal.class),
            new JILCaster<JILInt>(JILInt.class),
            new JILCaster<JILString>(JILString.class)
        };

        objectConstructor = new JILConstructor[]{
            new JILConstructor<JILBoolean>(new JILFunction<JILBoolean>(){
                    public JILBoolean call() {
                        return new JILBoolean(false, false);
                    }
                },
                new JILFunction<JILBoolean>(){
                    public JILBoolean call() {
                        return new JILBoolean(true);
                    }
                }
            ),
            new JILConstructor<JILChar>(new JILFunction<JILChar>(){
                    public JILChar call() {
                        return new JILChar('\0', false);
                    }
                },
                new JILFunction<JILChar>(){
                    public JILChar call() {
                        return new JILChar(true);
                    }
                }
            ),
            new JILConstructor<JILDecimal>(new JILFunction<JILDecimal>(){
                    public JILDecimal call() {
                        return new JILDecimal(0.0, false);
                    }
                },
                new JILFunction<JILDecimal>(){
                    public JILDecimal call() {
                        return new JILDecimal(true);
                    }
                }
            ),
            new JILConstructor<JILInt>(new JILFunction<JILInt>(){
                public JILInt call() {
                    return new JILInt(0, false);
                }
            },
            new JILFunction<JILInt>(){
                public JILInt call() {
                    return new JILInt(true);
                }
            }),
            new JILConstructor<JILString>(new JILFunction<JILString>(){
                public JILString call() {
                    return new JILString("bruh", false);
                }
            },
            new JILFunction<JILString>(){
                public JILString call() {
                    return new JILString(true);
                }
            })
        };
        
        try {
            logger = new IO(logFile);
            userFunctions = new HashMap<>();
            userProgram = createTokens(readFile(userFile));
            parser = new Parser(this);
            run(userProgram);
        }
        catch(IOException e) {
            System.out.println("The input file doesn't exist. Make sure the path is correct.\nThe path used was: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        // if(args == null || args.length == 0) {
        //     System.out.println("No arguments found.\nUsage: java jil -f <userFile>.jil");
        //     return;
        // }

        String userFile = null;
        String logFile = null;
        
        if(args == null || args.length == 0) {
            Scanner in = new Scanner(System.in);
            System.out.println("Enter the arguments: ");
            args = in.nextLine().split(" ");
            in.close();
        }

        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-l")) {
                try {
                    logFile = args[i + 1];
                    i++;
                }
                catch(ArrayIndexOutOfBoundsException e) {
                    System.out.println("No arguments were '-l' flag.");
                    System.exit(1);
                }
            }
            if(args[i].equals("-f")) {
                i++;

                try {
                    if(args[i].endsWith(".jil") || args[i].endsWith(".jl"))
                        userFile = args[i];
                }
                catch(ArrayIndexOutOfBoundsException e) {
                    System.out.println("No arguments for the '-f' flag.");
                }
            }
            if(args[i].endsWith(".jil") || args[i].endsWith(".jl"))
                userFile = args[i];
        }

        if(userFile == null) {
            System.out.println("FATAL ERROR: No filename was found.");
            return; 
        }
        
        new jil(userFile, logFile);
    }

    public String readFile(String userFile) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(userFile));

        if (hadError) System.exit(65);

        return new String(bytes, Charset.defaultCharset());
    }

    private ArrayList<Token> createTokens(final String source) { return new Lexer(source).scanTokens(); }

    private void run(final ArrayList<Token> tokens) {
        int index = 0;

        for (index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            switch(token.type) {
                case TokenType.IMPORT: {
                    index = handleImports(tokens, index);
                    break;
                }
                case TokenType.DECLARE: {
                    index = handleDeclare(tokens, index);
                    break;
                }
                case TokenType.INITIAL: {
                    index = handleInitial(tokens, index);
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if(varNames != null)
        for(int i = 0; i < varNames.length; i++) {
            System.out.println(varNames[i] + " -> " + varValues[i]);
        }
    }
    
    public static void error(final int line, final String message) { report(line, "", message); System.exit(-1); }
    
    private static void report(final int line, final String where, final String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
    
    public int handleImports(final ArrayList<Token> tokens, int index) {
        while(tokens.get(index).type != TokenType.RIGHT_BRACE) {
            index++;
        }
        
        return index;
    }
    
    public int handleVariable(final ArrayList<Token> tokens, int index) {
        while(!tokens.get(index).lexeme.equals("\n")) {
            System.out.println(tokens.get(index));
            index++;
        }
        
        return index;
    }
    
    // THIS FUNCTION ONLY HANDLES DECLARATION.
    public int handleDeclare(final ArrayList<Token> tokens, int index) {
        if(tokens.get(index + 1).type != TokenType.LEFT_BRACE)
            error(tokens.get(index).line, "Expected { found " + tokens.get(index));
        
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
        
        for(; tokens.get(index).type != TokenType.RIGHT_BRACE; index++) {          
            int inc = 0;
            boolean constant = false;
            
            if(tokens.get(index).type == TokenType.VARIABLE) {
                inc++;
                tok = tokens.get(index + inc);
                if(!(tok.type == TokenType.COLON))
                error(tok.line, "Expected ':' found " + tok);
                
                inc++;
                tok = tokens.get(index + inc);
                if(tok.type == TokenType.CONST) {
                    inc++;
                    tok = tokens.get(index + inc);
                    
                    if(!dataList.contains(tok.type)) error(tok.line, "Found CONST but, expected data type, found " + tok);
                    constant = true;
                }
                else if(!dataList.contains(tok.type))
                error(tok.line, "Expected data type, found " + tok);
                
                var.put(tokens.get(index).lexeme, tok.type);
                isConst.put(tokens.get(index).lexeme, constant);
            }
        }
        
        ArrayList<Map.Entry<String, TokenType>> sortable = new ArrayList<>(var.entrySet());        
        Collections.sort(sortable, (x, y) -> x.getKey().compareTo(y.getKey()));
        
        varNames = new String[var.size()];
        varValues = new JILType[var.size()];
        varCaster = new JILCaster[var.size()];
        
        for(Map.Entry<String, TokenType> ele : sortable)
        {
            varNames[arrIndex] = ele.getKey();
            TokenType type = ele.getValue();
            
            if(isConst.get(ele.getKey()))
                varValues[arrIndex] = (JILType) this.objectConstructor[type.ordinal()].constructConstant();
            else
                varValues[arrIndex] = (JILType) this.objectConstructor[type.ordinal()].constructDefault();
            varCaster[arrIndex++] = this.objectCaster[type.ordinal()];
        }
        
        System.out.println("Variable names: " + Arrays.toString(varNames));
        
        return index;
    }

    private int handleInitial(final ArrayList<Token> tokens, int index) {
        if(tokens.get(index + 1).type != TokenType.LEFT_BRACE)
            error(tokens.get(index).line, "Expected { found " + tokens.get(index));

        Token curToken = null;

        while((curToken = tokens.get(index)).type != TokenType.RIGHT_BRACE) {
            if(curToken.type == TokenType.VARIABLE) {
                index = handleAssignment(tokens, index);
                continue;
            }
            index++;
        }

        return index;
    }

    private int handleAssignment(final ArrayList<Token> tokens, int index) {
        Token curToken = tokens.get(index);
        JILType curVariable = varValues[getVariable(curToken.lexeme)];

        try {
            while((curToken = tokens.get(index)).type != TokenType.NEWLINE && curToken.type != TokenType.SEMICOLON) {
                Token tok;
                int inc = 0;

                if(curToken.type == TokenType.VARIABLE) {
                    curVariable = varValues[getVariable(curToken.lexeme)];

                    inc++;
                    tok = tokens.get(index + inc);
                    if(tok.type != TokenType.ASSIGNMENT)
                        error(tok.line, "Expected '=', but found: " + tok.lexeme);

                    inc++;
                    tok = tokens.get(index + inc);

                    if(tok.type == TokenType.VARIABLE) {
                        checkCompatible(getVariableValue(curToken).dataType, getVariableValue(tok).dataType, curToken.line);
                        index = handleAssignment(tokens, index + inc);
                        curVariable.setValue(getVariableValue(tok).getValue((short) curToken.line), (short) curToken.line);
                        continue;
                    }
                    else {
                        JILType value = parser.parseExpression(getVariableValue(curToken), tokens, index + inc);
                        curVariable.setValue(value.getValue(tok.line), tok.line);

                        while(tokens.get(index++).type != TokenType.NEWLINE);

                        return index;
                    }
                }
                index++;
            }
        }
        catch(WrongCastException e) {
            error(e.line, e.getMessage());
        }
        catch(ConstantValueEditException e) {
            error(e.line, e.getMessage());
        }
        catch(ValueNotSetException e) {
            error(e.line, e.getMessage());
        }

        return index;
    }

    public int getVariable(final String search) {
        int start = 0, end = varNames.length - 1, mid = (start + end) / 2;
        
        while(start <= end) {
            mid = (start + end) / 2;

            if(mid > end) break;

            if(search.equals(varNames[mid])) return mid;

            if(varNames[mid].compareTo(search) < 0) start = mid + 1;
            else if(varNames[mid].compareTo(search) > 0) end = mid - 1;
        }

        return -1;
    }

    public JILType getVariableValue(final Token search) {
        int index = getVariable(search.lexeme);

        if(index != -1) return varValues[index];
        
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