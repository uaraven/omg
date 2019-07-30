package net.ninjacat.omg.reflect;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.function.Function;

import static io.vavr.API.*;

/**
 * Basic type conversions
 */
public final class TypeUtils {
    private TypeUtils() {
    }

    private static final Set<Class> INT_CLASSES = HashSet.of(
            Integer.class,
            Long.class,
            Short.class,
            Byte.class,
            Character.class,
            int.class,
            long.class,
            byte.class,
            short.class,
            char.class
    );
    private static final Set<Class> FLOAT_CLASSES = HashSet.of(
            Float.class,
            Double.class,
            float.class,
            double.class
    );


    /**
     * Widens type to one supported by matcher.
     * <p>
     * int, short, byte, char -> long
     * <p>
     * float -> double
     * <p>
     * All other types remain unchanged.
     *
     * @param aClass Type to widen
     * @return Widened type
     */
    static Class widen(final Class<?> aClass) {
        if (INT_CLASSES.contains(aClass)) {
            return Long.class;
        } else if (FLOAT_CLASSES.contains(aClass)) {
            return Double.class;
        } else {
            return aClass;
        }
    }

    /**
     * Converts value to a basic type which is {@link Long} for all integer types and {@link Double} for floating-point.
     * <p>
     * All other times remain unchanged
     *
     * @param value Value to convert to basic type
     * @return Value converted to basic type, if possible, otherwise original value
     */
    static Object convertToBasicType(final Object value) {
        return Match(value).of(
                Case($(TypeUtils::isInteger), i -> (long) (int) i),
                Case($(TypeUtils::isShort), i -> (long) (short) i),
                Case($(TypeUtils::isByte), i -> (long) (byte) i),
                Case($(TypeUtils::isChar), i -> (long) (char) i),
                Case($(TypeUtils::isFloat), i -> (double) (float) i),
                Case($(), Function.identity())
        );
    }

    private static boolean isInteger(final Object o) {
        return o instanceof Integer || o.getClass().equals(int.class);
    }

    private static boolean isShort(final Object o) {
        return o instanceof Short || o.getClass().equals(short.class);
    }

    private static boolean isByte(final Object o) {
        return o instanceof Byte || o.getClass().equals(byte.class);
    }

    private static boolean isChar(final Object o) {
        return o instanceof Character || o.getClass().equals(char.class);
    }

    private static boolean isFloat(final Object o) {
        return o instanceof Float || o.getClass().equals(float.class);
    }
}
