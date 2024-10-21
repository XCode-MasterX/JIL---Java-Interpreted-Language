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
        Lexer lexer = new Lexer(source);
        ArrayList<Token> tokens = lexer.scanTokens();

        for (Token token : tokens)
            System.out.println(token);
    }

    static void error(int line, String message) { report(line, "", message); }
    
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}