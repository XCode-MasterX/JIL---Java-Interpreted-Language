package JILUtils;

import JILBase.jil;
import JILDataTypes.JILType;
import JILDataTypes.JILArray;
import JILDataTypes.JILDecimal;
import JILDataTypes.JILInt;
import JILExceptions.ValueNotSetException;

import java.util.Comparator;

public class JILMath {
    public static JILDecimal sin(final JILType passed, final short line) {
        if(!(passed instanceof JILDecimal || passed instanceof JILInt)){
            jil.error(line, "The passed value is not a decimal or int.");
        }

        JILDecimal angle = (JILDecimal) passed;
        JILDecimal returnable = null;
        
        try {
            returnable = new JILDecimal(Math.sin((Double)angle.getValue(line)), false);
        }
        catch(ValueNotSetException e) {
            jil.logger.write(e.getMessage());
        }

        return returnable;
    }

    public static <T> T minimum(final JILArray<T> arr, final Comparator<T> compare, final short line) {
        Object array[] = arr.getArray();
        int index = 0;
        int minIndex = 0;
        T minObj = (T) array[minIndex], obj = (T) array[index];

        for(index = 1; index < array.length; index++) {
            obj = (T) array[index];
            if(compare.compare(minObj, obj) > 0)
                minIndex = index;
        }

        return (T) arr.get(minIndex);
    }
}