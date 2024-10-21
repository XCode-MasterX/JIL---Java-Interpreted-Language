package JILIO;

import JILDataTypes.*;
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

    public IO(final String outputFile) {
        functions = new HashMap<>();

        if(outputFile == null) pw = new PrintWriter(System.out);
        else createPrintWriter(outputFile);

        initializeFunctions();
    }

    public void initializeFunctions() {
        functions.put("writef", new JILFunction<JILVoid>() {
            public JILVoid call(Object... args) {
                writef((String)args[0], args);
                return null;
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
    public void write(JILBoolean output)                { pw.print(output); }
    public void write(JILDecimal output)                { pw.print(output); }
    public void write(JILInt output)                    { pw.print(output); }
    public void write(JILString output)                 { pw.print(output); }
    public void write(JILChar output)                   { pw.print(output); }
    public void write(String output)                    { pw.print(output); }
    public void writef(String format, Object... args)   { pw.printf(format, args); }
}