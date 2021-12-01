package Utils;

import java.lang.reflect.Field;

public class TypeUtils {
    public static boolean fieldIsTypeOf(Field field, Class<?> type) {
        return field.getType().isAssignableFrom(type);
    }
}
