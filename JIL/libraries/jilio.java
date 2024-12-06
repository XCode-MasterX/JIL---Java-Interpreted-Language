package libraries;

import java.util.HashMap;

import JILBase.Callable;
import JILBase.jil;
import JILIO.IO;

public class jilio extends Callable {
    private IO io;
    
    public jilio() {
        io = jil.logger;
        functions = new HashMap<>();

        functions.put( "writef", (line, args) -> {
            if(args.length < 1)
                jil.error(line, "Error on line: ");

            if(args[0] instanceof String x)
            {
                Object other[] = new Object[args.length - 1];
                for(int i = 1; i < args.length; i++)
                    other[i - 1] = args[i];

                io.writef(x, other);
            }
            return null;
        });

        functions.put("whatareyou", (line, args) -> {
            io.writeln("This is a library class called 'jilio'.");
            return null;
        });
    }
}
