package JILBase;

import JILDataTypes.*;
import JILIO.IO;
import JILUtils.JILFunction;

import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class jil {
    @SuppressWarnings("rawtypes")
    private HashMap<String, JILFunction> userFunctions = null;
    private String[] varNames;
    private Object[] varValues;
    private final String[] libraryNames = {"jilio", "jilbase", "jilutils"};
    private HashMap<String, Object> libraries;
    public static IO logger = null;
    private static boolean hadError = false;

    public jil(final String userFile, final String logFile) {
        try {
            readFile(userFile);
            logger = new IO(logFile);
            userFunctions = new HashMap<>();
        }
        catch(IOException e) {
            System.out.println("The input file doesn't exist. Make sure the path is correct.\nThe path used was: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        // if(args == null) {
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

    public static void readFile(String userFile) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(userFile));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    private static void run(String source) {
        final Lexer lexer = new Lexer(source);
        final ArrayList<Token> tokens = lexer.scanTokens();
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
                default: {
                    System.out.println(token);
                    break;
                }
            }
        }
    }

    static void error(int line, String message) { report(line, "", message); System.exit(-1); }
    
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static int handleImports(final ArrayList<Token> tokens, int index) {
        while(tokens.get(index).type != TokenType.RIGHT_BRACE) {
            System.out.println(tokens.get(index));
            index++;
        }

        return index;
    }

    public static int handleVariable(final ArrayList<Token> tokens, int index) {
        while(tokens.get(index).lexeme != "\n") {
            System.out.println(tokens.get(index));
            index++;
        }

        return index;
    }

    // THIS FUNCTION ONLY HANDLES DECLARATION.
    public static int handleDeclare(final ArrayList<Token> tokens, int index) {
        if(tokens.get(index + 1).type != TokenType.LEFT_BRACE)
            error(tokens.get(index).line, "Expected { found " + tokens.get(index));

        Token tok;
        ArrayList<String> var = new ArrayList<>();
        ArrayList<TokenType> varType = new ArrayList<>();

        final ArrayList<TokenType> dataList = new ArrayList<>();
        dataList.add(TokenType.INT);
        dataList.add(TokenType.DECIMAL);
        dataList.add(TokenType.BOOL);
        dataList.add(TokenType.CHAR);
        dataList.add(TokenType.STRING);

        for(; tokens.get(index).type != TokenType.RIGHT_BRACE; index++) {
            System.out.println(tokens.get(index));
            
            if(tokens.get(index).type == TokenType.VARIABLE) {
                TokenType type = null;

                tok = tokens.get(index + 1);
                if(!(tok.type == TokenType.COLON))
                    error(tok.line, "Expected : found " + tok);

                tok = tokens.get(index + 2);
                if(tok.type == TokenType.CONST) {
                    tok = tokens.get(index + 3);

                    if(!dataList.contains(tok.type)) error(tok.line, "Expected data type, found " + tok);
                    
                    type = tok.type;
                }
                else if(!dataList.contains(tok.type))
                        error(tok.line, "Expected data type, found " + tok);
                
                var.add(tokens.get(index).lexeme);
                varType.add(type);
                System.out.println("\n" + tokens.get(index).lexeme + " of type: " + tok.lexeme);
            }
        }

        return index;
    }
}