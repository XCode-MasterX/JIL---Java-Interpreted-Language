package JILBase;

import JILDataTypes.*;
import JILExceptions.ConstantValueEditException;
import JILExceptions.ValueNotSetException;
import JILExceptions.WrongCastException;
import JILIO.IO;
import JILUtils.JILConstructor;
import libraries.jilio;
import JILUtils.JILConstrcutorFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class jil {
    private String[] libraryNames = {};

    public static HashMap<String, Callable> libraryObjects;
    public static Program currentProgram;
    public static IO logger = null;
    public static Parser parser;
    public static final ArrayList<TokenType> dataList;
    public static final JILConstructor objectConstructor[];

    static {
        dataList = new ArrayList<>();
        dataList.add(TokenType.INT);
        dataList.add(TokenType.DECIMAL);
        dataList.add(TokenType.BOOL);
        dataList.add(TokenType.CHAR);
        dataList.add(TokenType.STRING);
        dataList.add(TokenType.VOID);
        
        objectConstructor = new JILConstructor[]{
            new JILConstructor<JILBoolean>(
                new JILConstrcutorFunction<JILBoolean>(){
                    public JILBoolean call() {
                        return new JILBoolean(false, false);
                    }
                },
                new JILConstrcutorFunction<JILBoolean>(){
                    public JILBoolean call() {
                        return new JILBoolean(true);
                    }
                }
            ),
            new JILConstructor<JILChar>(
                new JILConstrcutorFunction<JILChar>(){
                    public JILChar call() {
                        return new JILChar('\0', false);
                    }
                },
                new JILConstrcutorFunction<JILChar>(){
                    public JILChar call() {
                        return new JILChar(true);
                    }
                }
            ),
            new JILConstructor<JILDecimal>(
                new JILConstrcutorFunction<JILDecimal>(){
                    public JILDecimal call() {
                        return new JILDecimal(0.0, false);
                    }
                },
                new JILConstrcutorFunction<JILDecimal>(){
                    public JILDecimal call() {
                        return new JILDecimal(true);
                    }
                }
            ),
            new JILConstructor<JILInt>(
                new JILConstrcutorFunction<JILInt>(){
                    public JILInt call() {
                        return new JILInt(0, false);
                    }
                },
                new JILConstrcutorFunction<JILInt>(){
                    public JILInt call() {
                        return new JILInt(true);
                    }
                }
            ),
            new JILConstructor<JILString>(
                new JILConstrcutorFunction<JILString>(){
                    public JILString call() {
                        return new JILString("bruh", false);
                    }
                },
                new JILConstrcutorFunction<JILString>(){
                    public JILString call() {
                        return new JILString(true);
                    }
                }
            ),
            new JILConstructor<JILVoid>(
                new JILConstrcutorFunction<JILVoid>(){
                    public JILVoid call() {
                        return new JILVoid();
                    }
                },
                new JILConstrcutorFunction<JILVoid>(){
                    public JILVoid call() {
                        return new JILVoid();
                    }
                }
            ),
        };

        libraryObjects = new HashMap<>();
        createDefaultObjects();
        createLibraryObjects(getLibraryNames());
    }

    public jil(String userFile, String logFile) {
        logger = new IO(logFile);
        parser = new Parser(this);
        currentProgram = new Program(createTokens(readFile(userFile)));
    }

    public static void main(String args[]) {
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
                    jil.error(-1, "No arguments for the '-f' flag.");
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
        currentProgram.initialize();
        currentProgram.run();
    }

    public static String readFile(String userFile) {
        byte[] bytes = {};
        try {
            bytes = Files.readAllBytes(Paths.get(userFile));
            if(bytes.length == 0) 
                jil.error(Integer.MIN_VALUE, "Scary NUMBER!!! Anyways, I am not reading an empty file.");
        } catch (IOException e) {
            jil.error(-1, e.toString());
        }

        return new String(bytes, Charset.defaultCharset());
    }

    public static ArrayList<Token> createTokens(final String source) { return new Lexer(source).scanTokens(); }
    
    public static void error(final int line, final String message) { report(line, "", message); System.exit(-1); }
    
    private static void report(final int line, final String where, final String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
    }
    
    public static ReturnStructure<Integer, Variable[]> handleDeclaration(final ArrayList<Token> program, int index, final ArrayList<TokenType> finishingChars) {        
        final ArrayList<String> varNames = new ArrayList<>();
        final ArrayList<TokenType> varTypes = new ArrayList<>();
        final ArrayList<Boolean> isConstant = new ArrayList<>();
        Token current = program.get(index);

        while(!finishingChars.contains(current.type)) {
            if(current.type == TokenType.VARIABLE) {
                if(program.get(index).type != TokenType.COLON)
                    error(current.line, "Expected ':', found " + program.get(index));

                isConstant.add(program.get(++index).type == TokenType.CONST);
                
                if(dataList.contains(program.get(index).type) || dataList.contains(program.get(++index).type)) {
                    varTypes.add(program.get(index).type);
                    isConstant.add(false);
                }
                else
                    error(current.line, "Found unexpected token, " + program.get(index));
                

                varNames.add(current.lexeme);
            }

            current = program.get(index++);
        }

        Variable internalVariable[] = new Variable[varNames.size()];

        for(int i = 0; i < varNames.size(); i++) {
            JILType var = null;

            if(isConstant.get(i))
                var = (JILType) jil.objectConstructor[varTypes.get(i).ordinal()].constructDefault();
            else 
                var = (JILType) jil.objectConstructor[varTypes.get(i).ordinal()].constructConstant();

            internalVariable[i] = new Variable(varNames.get(i), var);
        }

        return new ReturnStructure<Integer, Variable[]>(index, internalVariable);
    }

    public static int handleInitial(final Program program, int index) {
        final ArrayList<Token> userProgram = program.userProgram;

        if(userProgram.get(index + 1).type != TokenType.LEFT_BRACE)
            jil.error(userProgram.get(index).line, "Expected { found " + userProgram.get(index));

        Token curToken = null;

        while((curToken = userProgram.get(index)).type != TokenType.RIGHT_BRACE) {
            if(curToken.type == TokenType.VARIABLE) {
                index = handleAssignment(program, index);
                continue;
            }
            index++;
        }

        return index;
    }

    public static int handleAssignment(final Program program, int index) {
        final ArrayList<Token> userProgram = program.userProgram;

        Token curToken = userProgram.get(index);
        JILType curVariable = program.getVariable(curToken.lexeme);

        try {
            while((curToken = userProgram.get(index)).type != TokenType.NEWLINE && curToken.type != TokenType.SEMICOLON) {
                Token tok;
                int inc = 0;

                if(curToken.type == TokenType.VARIABLE) {
                    curVariable = program.getVariable(curToken.lexeme);

                    inc++;
                    tok = userProgram.get(index + inc);
                    if(tok.type != TokenType.ASSIGNMENT)
                        jil.error(tok.line, "Expected '=', but found: " + tok.lexeme);

                    inc++;
                    tok = userProgram.get(index + inc);

                    if(tok.type == TokenType.VARIABLE) {
                        JILType variable = program.getVariable(tok.lexeme);
                        if(variable.wasSet) {
                            JILType value = jil.parser.parseExpression(variable, userProgram, index + inc);
                            curVariable.setValue(value.getValue(tok.line), tok.line);
                            while(userProgram.get(index++).type != TokenType.NEWLINE);
                            continue;
                        }
                        program.checkCompatible(variable.dataType, variable.dataType, curToken.line);
                        index = handleAssignment(program, index + inc);
                        curVariable.setValue(variable.getValue(curToken.line), (short) curToken.line);
                        continue;
                    }
                    else {
                        JILType value = jil.parser.parseExpression(program.getVariable(curToken.lexeme), userProgram, index + inc);
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

    private static String[] getLibraryNames() {
        try {
            File classnames = new File("libraries/classnames.txt");
            
            if(classnames.exists()) {
                return readFile(classnames.getAbsolutePath()).split(" ");
            }
            
            System.out.println("classnames.txt not found...");
            
            File currDir = new File("libraries/");
            if(!currDir.exists()) {
                currDir.createNewFile();
                jil.error(-1, "Libraries folder doesn't exist: " + currDir.getAbsolutePath());                
            }

            final StringBuilder sb = new StringBuilder();
            java.util.Arrays.stream(currDir.list()).map((x) -> {
                if(x.endsWith(".class"))
                    return x.substring(0, x.length() - 6);
                else
                    return null;
            }).filter((x) -> x != null).forEach((x) -> sb.append(x + " "));

            if(!classnames.exists()) {
                classnames.getParentFile().mkdir();
                classnames.createNewFile();
            }

            FileWriter writer = new FileWriter(classnames.getAbsolutePath());
            writer.write(sb.toString());
            writer.close();

            System.out.println(java.util.Arrays.toString(currDir.list()) + " " + sb.toString());
            return sb.toString().split(" ");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void createLibraryObjects(String libNames[]) {
        File f = new File("libraries/");
        URLClassLoader loader = null;

        try {
            loader = new URLClassLoader(new URL[]{f.toURI().toURL()});
            
            for(int i = 0; i < libNames.length; i++) {
                String library = libNames[i];
                libraryObjects.put(library, (Callable) loader.loadClass(library).getDeclaredConstructor().newInstance());
            }

            loader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Callable getLibraryObject(final int line, final String lookingFor) {
        Callable ret = libraryObjects.getOrDefault(lookingFor, null);

        if(ret != null) return ret;
        
        return new Callable(){
            public Object call(final String name, final int line, final Object... args) {
                jil.error(line, lookingFor + ", no such file exists.");
                return null;
            }
        };
    }

    private static void createDefaultObjects() {
        libraryObjects.put("jilio", new IO(null));
    }
}

class ReturnStructure<I, E> {
    public I x;
    public E y;

    public ReturnStructure(I i, E e) { x = i; y = e; }
}