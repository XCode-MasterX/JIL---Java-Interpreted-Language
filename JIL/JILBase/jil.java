package JILBase;

import JILDataTypes.*;
import JILIO.Logger;
import JILUtils.JILFunction;

import java.util.HashMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class jil {
    @SuppressWarnings("rawtypes")
    private HashMap<String, JILFunction> userFunctions = null;
    public static Logger logger = null;

    public jil(final String userFile, final String logFile) {
        try {
            readFile(userFile);
            logger = new Logger(logFile);
            userFunctions = new HashMap<>();
        }
        catch(FileNotFoundException e) {
            System.out.println("The input file doesn't exist. Make sure the path is correct.\nThe path used was: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String args[]) {        
        if(args == null) {
            System.out.println("No arguments found.\nUsage: java jil -f <userFile>.jil");
            return;
        }

        String userFile = null;
        String logFile = null;

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
        }

        new jil(userFile, logFile);
    }

    public static String readFile(String userFile) throws FileNotFoundException {
        File file = new File(userFile);

        if(!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());

        BufferedInputStream is = null;
        String program = null;
        try{
            is = new BufferedInputStream(new FileInputStream(file));
            program = new String(is.readAllBytes());
            is.close();
        }
        catch(IOException e) {
            logger.write("The file doesn't exist.");

            is = null;
            program = null;
            return program;
        }
        return program;
    }
}