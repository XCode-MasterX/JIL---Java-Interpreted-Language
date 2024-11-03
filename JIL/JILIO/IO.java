package JILIO;

import JILDataTypes.*;
import JILExceptions.FunctionCallException;
import JILExceptions.WrongCastException;
import JILUtils.JILFunction;

import java.util.HashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class IO {
    @SuppressWarnings("rawtypes")
    private HashMap<String, JILFunction> functions;
    private PrintWriter pw;
    private final JILVoid returnVoid;

    public IO(final String outputFile) {
        returnVoid = new JILVoid();
        functions = new HashMap<>();

        if(outputFile == null) pw = new PrintWriter(System.out);
        else createPrintWriter(outputFile);

        initializeFunctions();
    }

    public void initializeFunctions() {
        functions.put("writef", new JILFunction<JILVoid>() {
            public JILVoid call(final short line, Object... args) throws Exception{
                if(!(args[0] instanceof String))
                    throw new WrongCastException("The first argument needs to be String for a proper output.", line);

                Object arr[] = new Object[args.length];

                for(int i = 1; i < args.length; i++)
                    arr[i - 1] = args[0];

                writef((String)args[0], arr);
                return returnVoid;
            }
        });

        functions.put("write", new JILFunction<JILVoid>() {
            public JILVoid call(final short line, Object... args) throws FunctionCallException{
                if(args.length > 1)
                    throw new FunctionCallException("Too many arguments. This function only accepts one.", line);

                write(args[0]);
                return returnVoid;
            }
        });

        functions.put("writeln", new JILFunction<JILVoid>() {
            public JILVoid call (final short line, Object... args) throws FunctionCallException{
                if(args.length > 1)
                    throw new FunctionCallException("Too many arguments. This function only accepts one.", line);

                if(args.length == 1) writeln(args[0]);
                else if(args.length == 0) writeln();
                
                return returnVoid;
            }
        });
    }

    private void createPrintWriter(final String outputFile) {        
        try {
            File f = new File(outputFile);

            if(!f.exists()) f.createNewFile();
            else {
                if(f.isDirectory())
                    f = new File(f.getAbsolutePath() + "/outputFile.jlo");
            }

            pw = new PrintWriter(f);
        }
        catch(FileNotFoundException e) {
            System.out.println("The file was not found." + e.getMessage());
        }
        catch(IOException e) {
            System.out.println("There was a problem when trying to create the logging file. " + e.getMessage());
        }
    }

    public void writeln()                               { pw.println(); }
    public void writeln(Object output)                  { pw.println(output); }
    public void write(Object output)                    { pw.print(output); }
    public void writef(String format, Object... args)   { pw.printf(format, args); }

    public Object call(final String funcName, final short line, Object... args) {
        Object returnValue = null;

        try {
            returnValue = functions.get(funcName).call(line, args);
        }
        catch(Exception e) {
            // If error occurs then print and stop the execution.
            // If the programmer makes a mistake make it known very well.
            System.out.println(e.toString());
            System.exit(1);
        }

        return returnValue;
    }
}