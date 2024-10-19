package JILUtils;

import JILBase.jil;
import JILDataTypes.*;
import JILExceptions.WrongCastException;

public class JILMath {
    public static JILDecimal sin(JILNumber passed) {
        JILDecimal angle = (JILDecimal) passed;

        JILDecimal returnable = null;
        
        try {
            returnable = new JILDecimal(Math.sin(angle.getValue()), false);
        }
        catch(WrongCastException e) {
            jil.logger.write(e.getMessage());
        }

        return returnable;
    }
    
    //public static JILDecimal cos() {
//
    //}
}
