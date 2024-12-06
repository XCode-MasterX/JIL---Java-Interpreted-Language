package JILBase;

import java.util.HashMap;

import JILUtils.JILFunction;

public abstract class Callable {
    public HashMap<String, JILFunction> functions;
    
    public Object call(final String name, final int line, Object... args) {
        try {
        if(functions.getOrDefault(name, null) != null)
            return functions.get(name).call(line, args);
        }
        catch(Exception e) {
            jil.logger.writeln(e.toString());
        }

        jil.error(line, String.format("There's no function named: \'%s\'"));
        return null;
    }
}
