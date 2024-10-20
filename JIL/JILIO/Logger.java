package JILIO;

import JILDataTypes.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    PrintWriter pw;
    public Logger(final String outputFile) {
        if(outputFile == null) pw = new PrintWriter(System.out);
        else createPrintWriter(outputFile);
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

    public void writeln()                   { pw.println(); }
    public void write(JILBoolean output)    { pw.print(output); }
    public void write(JILDecimal output)    { pw.print(output); }
    public void write(JILInt output)        { pw.print(output); }
    public void write(JILString output)     { pw.print(output); }
    public void write(JILChar output)       { pw.print(output); }
    public void write(String output)        { pw.print(output); }
    public void writeFormatted(String output, Object... extra) { pw.printf(output, extra); }
}